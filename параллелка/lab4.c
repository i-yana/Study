#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <mpi.h>
#define M 700

int main(int args, char **argv) {
    int size, rank;
    MPI_Status status;
    double tv1, tv2;
    double time;
    FILE * file;

    MPI_Init(&args, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int rowCount = M / size + (rank < M % size);
    double coef, res;

    double** A = (double**)malloc(sizeof(double*)*rowCount);

    for(int i=0;i<rowCount;i++){
        A[i] = (double*)malloc(sizeof(double)*(M+1));
    }

    double* V = (double*)malloc(sizeof(double)*(M+1));

    int *recvcounts = (int*)malloc(sizeof(int)*size);
    int *offset = (int*)malloc(sizeof(int)*size);

    for (int i = 0; i < size; ++i) {
        recvcounts[i] = M/size + (i < M % size);//nf_line у меня
    }
    offset[0] = 0;
    for (int i = 1; i < size; ++i) {
        offset[i] = offset[i - 1] + recvcounts[i - 1];
    }

    int *concretOffset = (int *) malloc(sizeof(int) * rowCount);

    concretOffset[0] = rank;
    for(int i = 1; i < rowCount; i++){
        concretOffset[i] = concretOffset[i-1] + size;
    }

    int * allOffset = (int *)malloc(sizeof(int) * M);

    MPI_Gatherv(concretOffset, recvcounts[rank], MPI_INT, allOffset, recvcounts, offset, MPI_INT, 0, MPI_COMM_WORLD);
    for(int i = 0; i < rowCount; i++){
        for(int j = 0; j < M; j++){
            if(j == concretOffset[i]){
                A[i][j] = 2;
            }
            else
                A[i][j] = 1;
        }
        A[i][M] = M + 1;
    }

    int normalLineCount = 0;

    if((M % size) == 0)
        normalLineCount = recvcounts[0];
    else
        normalLineCount = recvcounts[0] - 1;

    tv1 =  MPI_Wtime();

    for(int k = 0; k < normalLineCount; k++){
        for(int p = 0; p < size; p++){
            if(rank == p){
                coef = 1 / A[k][size * k + p];
                for(int j = M; j >= size * k + p; j--)
                    A[k][j] = A[k][j] * coef;
                for(int j = 0; j <= M; j++)
                    V[j] = A[k][j];
                MPI_Bcast(V, M+1, MPI_DOUBLE, p, MPI_COMM_WORLD);
                for(int i = k+1; i < recvcounts[rank]; i++){
                    for(int j = M; j >= size*k + p; j--)
                       A[i][j] = A[i][j] - A[i][size*k+p]*A[k][j];
                    }
            }
            else if(rank < p){
                MPI_Bcast(V, M+1, MPI_DOUBLE, p, MPI_COMM_WORLD);
                for(int i = k+1; i < recvcounts[rank]; i++){
                    for(int j = M; j >= size*k + p; j--)
                        A[i][j] = A[i][j] - A[i][size*k+p]*V[j];
                }


            }
            else if(rank > p){
                MPI_Bcast(V, M+1, MPI_DOUBLE, p, MPI_COMM_WORLD);
                for(int i = k; i < recvcounts[rank]; i++){
                    for(int j = M; j >= size*k+p; j--)
                        A[i][j] = A[i][j] - A[i][size*k+p]*V[j];
                }
            }

        }
    }

    double *ResultOfBadLine = (double*)malloc(sizeof(double) * size);
    int numberLastBadLine=0;
    if(M % size != 0){ //оставшиеся чтроки обрабатываем отдельно

        double* lastLine = (double*)malloc(sizeof(double)*(M+1));
        for(int i =1; i < size; i++){
            if(recvcounts[i]==recvcounts[0])
                numberLastBadLine = i;
            else
                break;
        }
        for(int p = 0; p <= numberLastBadLine; p++){
            if(p == rank){
                for(int j = 0; j < M+1; j++){
                    lastLine[j] = A[normalLineCount][j];
                }
            }
        }

        for(int p = 0; p <= numberLastBadLine ; p++){
            if(rank == p){
                coef = 1.0/lastLine[normalLineCount*size + p];
                for(int j = M; j >= normalLineCount*size + p; j--)
                    lastLine[j] = lastLine[j] * coef;
                for(int  d = p+1; d <= numberLastBadLine; d++)
                    MPI_Send(lastLine, M+1, MPI_DOUBLE, d, 1, MPI_COMM_WORLD);
            }
            else if(rank > p && rank <=numberLastBadLine){
                MPI_Recv(V, M+1, MPI_DOUBLE, p, 1, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
                for(int  j = M; j >= normalLineCount*size + p; j--)
                    lastLine[j] = lastLine[j]-lastLine[normalLineCount*size + p]*V[j];
            }
        }

        double res;

        for(int p = numberLastBadLine; p >= 0; p--){
            if(p == rank){
                for(int d = p - 1; d >= 0; d--)
                    MPI_Send(&lastLine[M], 1, MPI_DOUBLE, d, 2, MPI_COMM_WORLD);
            }
            else if(rank < p){
                MPI_Recv(&res, 1, MPI_DOUBLE, p, 2, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
                lastLine[M] -= res*lastLine[normalLineCount*size + rank+1];
            }
        }

        for(int i = 0; i <= numberLastBadLine; i++){
            if(rank == i){
                for(int j = 0; j < M+1; j++)
                    A[normalLineCount][j] = lastLine[j];
            }
        }

        MPI_Allgather(&lastLine[M], 1, MPI_DOUBLE, ResultOfBadLine, 1, MPI_DOUBLE, MPI_COMM_WORLD);

        free(lastLine);
    }
    //збс работает!!!!!

    //ТУТ БУДЕТ ОБРАТНЫЙ ХОД ДЛЯ ОЧЕНЬ ПЛОХИХ СТРОК!!!

    for(int i = 0; i < normalLineCount; i++){
        int border = M - numberLastBadLine - 1;
        if(concretOffset[i] > M - numberLastBadLine - 1)
            border = concretOffset[i];
        for(int j = border; j < M; j++){
            A[i][M] -= A[i][j]*ResultOfBadLine[M - j -1];
        }
    }

    //теперь обратный ход остальной
    double R = 0;
    for(int k1 = normalLineCount-2, k = normalLineCount-1; k >= 0; k--,k1--)
    {
        for(int p = size-1; p >= 0; p--){
            if(rank == p){
                R = A[k][M];
                MPI_Bcast(&R,1,MPI_DOUBLE,p,MPI_COMM_WORLD);
                for(int  i = k-1; i >= 0; i--)
                    A[i][M]-= A[k][M] * A[i][size*k+p];
            }

            else if(rank < p){
                MPI_Bcast(&R,1,MPI_DOUBLE,p,MPI_COMM_WORLD);
                for(int i = k; i >= 0; i--)
                    A[i][M] -= R * A[i][ size*k+p];
            }

            else if(rank > p){
                MPI_Bcast(&R,1,MPI_DOUBLE,p,MPI_COMM_WORLD);
                for(int i = k1; i >= 0; i--)
                    A[i][M] -= R * A[i][size*k+p];
            }
        }
    }

    tv2 =  MPI_Wtime();

    double *partResult = (double*)malloc(sizeof(double*)*recvcounts[rank]);
    for(int i = 0; i < recvcounts[rank]; i++){
        partResult[i] = A[i][M];
    }
    double *result = (double*)malloc(sizeof(double*)*M);

    MPI_Gatherv(partResult, recvcounts[rank], MPI_DOUBLE, result, recvcounts, offset, MPI_DOUBLE, 0, MPI_COMM_WORLD);

    double *finalResult = (double*)malloc(sizeof(double*)*M);
    for(int p = 0; p < size; p++){
        for(int i = 0; i < recvcounts[p]; i++)
            finalResult[i*size + p] = result[i];
    }
    if(rank==0){
        printf("result\n");
        for(int i = 0; i < M; i++)
            printf("%.02f\n",finalResult[i]);
    }

    if(rank==0){
           double l1 = 0, l2=0, l_inf = 0;
           for(int i = 0; i < M; i++){
               l1 += fabs(finalResult[i] - 1);
               l2 += (finalResult[i] - 1) * (finalResult[i] - 1);
               if(l_inf < fabs(finalResult[i] - 1))
                   l_inf = fabs(finalResult[i] - 1);
           }
           l2 = sqrt(l2);
           printf("l1 = %e, l1(MAE) = %e, l2 = %e, l_inf = %e\n", l1, l1/M, l2, l_inf);
           file = fopen("output4_1.csv","a");
           fprintf( file, "%d,%f,l1 = %e,l1(MAE) = %e,l2 = %e,l_inf = %e\n",size, (tv2-tv1),l1, l1/M, l2, l_inf );
           fclose(file);
       }

    free(V);
    free(result);
    free(finalResult);
    free(partResult);
    free(offset);
    free(ResultOfBadLine);
    free(concretOffset);
    free(recvcounts);
    for(int i=0; i<rowCount; i++){
        free(A[i]);
    }
    free(A);
    MPI_Finalize();
    return(0);
}
