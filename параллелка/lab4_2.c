#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <mpi.h>
#define M 3000

int main(int args, char **argv) {
    int size, rank;
    double tv1, tv2;
    int i,k,p,j,d;
    MPI_Init(&args, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int rowCount = M / size + (rank < M % size);
    double coef;

    double** A = (double**)malloc(sizeof(double*)*rowCount);

    for(i=0;i<rowCount;i++){
        A[i] = (double*)malloc(sizeof(double)*(M+1));
    }

    double* V = (double*)malloc(sizeof(double)*(M+1));

    int *recvcounts = (int*)malloc(sizeof(int)*size);
    int *displs = (int*)malloc(sizeof(int)*size);

    for (i = 0; i < size; ++i) {
        recvcounts[i] = M/size + (i < M % size);
    }
    displs[0] = 0;
    for (i = 1; i < size; ++i) {
        displs[i] = displs[i-1] + recvcounts[i-1];
    }

    int *displsLineFromRootProcess = (int*) malloc(sizeof(int) * rowCount);

    displsLineFromRootProcess[0] = rank;
    for(i = 1; i < rowCount; i++){
        displsLineFromRootProcess[i] = displsLineFromRootProcess[i-1] + size;
    }

    int * allOffset = (int *)malloc(sizeof(int) * M);

    MPI_Gatherv(displsLineFromRootProcess, recvcounts[rank], MPI_INT, allOffset, recvcounts, displs, MPI_INT, 0, MPI_COMM_WORLD);
    for(i = 0; i < rowCount; i++){
        for(j = 0; j < M; j++){
            A[i][j] = (j == displsLineFromRootProcess[i]) ? 2.0:1.0;
        }
        A[i][M] = (1.0)*M + 1.0;
    }

    int integerOfLines = 0;

    if((M % size) == 0)
        integerOfLines = recvcounts[0];
    else
        integerOfLines = recvcounts[0] - 1;

    tv1 =  MPI_Wtime();

    for(k = 0; k < integerOfLines; k++){
        for(p = 0; p < size; p++){
            if(rank == p){
                coef = 1 / A[k][size * k + p];
                for(j = M; j >= size * k + p; j--)
                    A[k][j] = A[k][j] * coef;
                for(j = 0; j <= M; j++)
                    V[j] = A[k][j];
                MPI_Bcast(V, M+1, MPI_DOUBLE, p, MPI_COMM_WORLD);
                for(i = k+1; i < recvcounts[rank]; i++){
                    for(j = M; j >= size*k + p; j--)
                       A[i][j] = A[i][j] - A[i][size*k+p]*A[k][j];
                    }
            }
            else if(rank < p){
                MPI_Bcast(V, M+1, MPI_DOUBLE, p, MPI_COMM_WORLD);
                for(i = k+1; i < recvcounts[rank]; i++){
                    for(j = M; j >= size*k + p; j--)
                        A[i][j] = A[i][j] - A[i][size*k+p]*V[j];
                }


            }
            else if(rank > p){
                MPI_Bcast(V, M+1, MPI_DOUBLE, p, MPI_COMM_WORLD);
                for(i = k; i < recvcounts[rank]; i++){
                    for(j = M; j >= size*k+p; j--)
                        A[i][j] = A[i][j] - A[i][size*k+p]*V[j];
                }
            }

        }
    }

    double *resultOfExtraLines = (double*)malloc(sizeof(double) * size);
    int numberOfRemainingLines=0;
    
    if(M % size != 0){ 
        double* extraLine = (double*)malloc(sizeof(double)*(M+1));
        for(i =1; i < size; i++){
            if(recvcounts[i]==recvcounts[0])
                numberOfRemainingLines = i;
            else
                break;
        }
        for(p = 0; p <= numberOfRemainingLines; p++){
            if(p == rank){
                for(j = 0; j < M+1; j++){
                    extraLine[j] = A[integerOfLines][j];
                }
            }
        }

        for(p = 0; p <= numberOfRemainingLines ; p++){
            if(rank == p){
                coef = 1.0/extraLine[integerOfLines*size + p];
                for(j = M; j >= integerOfLines*size + p; j--)
                    extraLine[j] = extraLine[j] * coef;
                for(d = p+1; d <= numberOfRemainingLines; d++)
                    MPI_Send(extraLine, M+1, MPI_DOUBLE, d, 1, MPI_COMM_WORLD);
            }
            else if(rank > p && rank <=numberOfRemainingLines){
                MPI_Recv(V, M+1, MPI_DOUBLE, p, 1, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
                for(j = M; j >= integerOfLines*size + p; j--)
                    extraLine[j] = extraLine[j]-extraLine[integerOfLines*size + p]*V[j];
            }
        }

        double res;

        for(p = numberOfRemainingLines; p >= 0; p--){
            if(p == rank){
                for(d = p - 1; d >= 0; d--)
                    MPI_Send(&extraLine[M], 1, MPI_DOUBLE, d, 2, MPI_COMM_WORLD);
            }
            else if(rank < p){
                MPI_Recv(&res, 1, MPI_DOUBLE, p, 2, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
                extraLine[M] -= res*extraLine[integerOfLines*size + rank+1];
            }
        }

        for(i = 0; i <= numberOfRemainingLines; i++){
            if(rank == i){
                for(j = 0; j < M+1; j++)
                    A[integerOfLines][j] = extraLine[j];
            }
        }

        MPI_Allgather(&extraLine[M], 1, MPI_DOUBLE, resultOfExtraLines, 1, MPI_DOUBLE, MPI_COMM_WORLD);

        free(extraLine);
    }
    
    for(i = 0; i < integerOfLines; i++){
        int border = M - numberOfRemainingLines - 1;
        if(displsLineFromRootProcess[i] > M - numberOfRemainingLines - 1)
            border = displsLineFromRootProcess[i];
        for(j = border; j < M; j++){
            A[i][M] -= A[i][j]*resultOfExtraLines[M - j -1];
        }
    }

    double R = 0;
    int k1;
    for(k1 = integerOfLines-2, k = integerOfLines-1; k >= 0; k--,k1--)
    {
        for(p = size-1; p >= 0; p--){
            if(rank == p){
                R = A[k][M];
                MPI_Bcast(&R,1,MPI_DOUBLE,p,MPI_COMM_WORLD);
                for(i = k-1; i >= 0; i--)
                    A[i][M]-= A[k][M] * A[i][size*k+p];
            }

            else if(rank < p){
                MPI_Bcast(&R,1,MPI_DOUBLE,p,MPI_COMM_WORLD);
                for(i = k; i >= 0; i--)
                    A[i][M] -= R * A[i][ size*k+p];
            }

            else if(rank > p){
                MPI_Bcast(&R,1,MPI_DOUBLE,p,MPI_COMM_WORLD);
                for(i = k1; i >= 0; i--)
                    A[i][M] -= R * A[i][size*k+p];
            }
        }
    }

    tv2 =  MPI_Wtime();

    double *partResult = (double*)malloc(sizeof(double*)*recvcounts[rank]);
    for(i = 0; i < recvcounts[rank]; i++){
        partResult[i] = A[i][M];
    }
    double *result = (double*)malloc(sizeof(double*)*M);

    MPI_Gatherv(partResult, recvcounts[rank], MPI_DOUBLE, result, recvcounts, displs, MPI_DOUBLE, 0, MPI_COMM_WORLD);

    double *finalResult = (double*)malloc(sizeof(double*)*M);
    for(p = 0; p < size; p++){
        for(i = 0; i < recvcounts[p]; i++)
            finalResult[i*size + p] = result[i];
    }

    if(rank==0){
           double l1 = 0, l2=0, l_inf = 0;
           for(i = 0; i < M; i++){
               l1 += fabs(finalResult[i] - 1);
               l2 += (finalResult[i] - 1) * (finalResult[i] - 1);
               if(l_inf < fabs(finalResult[i] - 1))
                   l_inf = fabs(finalResult[i] - 1);
           }
           l2 = sqrt(l2);
           printf("time = %f\nl1 = %e, l1(1/SIZE) = %e, l2 = %e, l_inf = %e\n",tv2 - tv1, l1, l1/M, l2, l_inf);
       }

    free(V);
    free(result);
    free(finalResult);
    free(partResult);
    free(displs);
    free(resultOfExtraLines);
    free(displsLineFromRootProcess);
    free(recvcounts);
    for(i=0; i<rowCount; i++){
        free(A[i]);
    }
    free(A);
    MPI_Finalize();
    return 0;
}