#include<stdio.h>
#include<stdlib.h>
#include<mpi.h>
#include<math.h>
#include<sys/time.h>

#define M 3000

int main(int args, char **argv) {
    int size, rank, i, j, k, d, p;
    MPI_Status status;
    double time1, time2;
    MPI_Init(&args, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int rowCount = M / size + (rank < M % size);
    double coef, res;

    double** A = (double**)malloc(sizeof(double*)*rowCount);

    for(i=0;i<rowCount;i++){
        A[i] = (double*)malloc(sizeof(double)*(M+1));
    }

    double* V = (double*)malloc(sizeof(double)*(M+1));

    int *recvcounts = (int*)malloc(sizeof(int)*size);
    int *offset = (int*)malloc(sizeof(int)*size);

    for (i = 0; i < size; ++i) {
        recvcounts[i] = M/size + (i < M % size);
    }
    offset[0] = 0;
    for (i = 1; i < size; ++i) {
        offset[i] = offset[i - 1] + recvcounts[i - 1];
    }

    for(i = 0; i < rowCount; i++){
        for(j = 0; j < M; j++){
            if((offset[rank]+i) == j)
                A[i][j] = 2.0;
            else
                A[i][j] = 1.0;
        }
        A[i][M] = 1.0*(M)+1.0;
    }


    time1 = MPI_Wtime();

    for(p = 0; p < size; p++){
        for(k = 0; k < recvcounts[p]; k++){
            if(rank == p){
                coef = 1.0/A[k][offset[p]+k];
                for(j = M; j >= offset[p]+k; j--)
                    A[k][j] = A[k][j] * coef;
                for(d = p+1; d < size; d++)
                    MPI_Send(&A[k][0], M+1, MPI_DOUBLE, d, 1, MPI_COMM_WORLD);
                for(i = k+1; i < rowCount; i++){
                    for(j = M; j >= offset[p]+k; j--)
                        A[i][j] = A[i][j]-A[i][offset[p]+k]*A[k][j];
                    }
                }
            else if(rank > p){
                MPI_Recv(V, M+1, MPI_DOUBLE, p, 1, MPI_COMM_WORLD, &status);
                for(i = 0; i < rowCount; i++){
                    for(j = M; j >= offset[p]+k; j--)
                        A[i][j] = A[i][j]-A[i][offset[p]+k]*V[j];
                }
            }
        }
    }

    MPI_Barrier(MPI_COMM_WORLD);

    for(p = size-1; p >= 0; p--){
        for(k = recvcounts[p]-1; k >= 0; k--){
            if(rank == p){
                for(d = p-1; d >= 0; d--)
                MPI_Send(&A[k][M], 1, MPI_DOUBLE, d, 2, MPI_COMM_WORLD);
                for(i = k-1; i >= 0; i--)
                    A[i][M] -= A[k][M]*A[i][offset[p]+k];
            }
            else{
                if(rank < p){
                    MPI_Recv(&res, 1, MPI_DOUBLE, p, 2, MPI_COMM_WORLD, &status);
                    for(i = rowCount-1; i >= 0; i--)
                        A[i][M] -= res*A[i][offset[p]+k];

                }
            }
        }
    }

    time2 = MPI_Wtime();
    if(rank == 0){
    	printf("time - %f\n",time2-time1);
    }
    MPI_Barrier(MPI_COMM_WORLD);
    double *local_x = (double*)malloc(sizeof(double)*rowCount);
    for(i=0;i<rowCount;i++)
    {
	local_x[i] = A[i][M];
    }
    double *x = (double*)malloc(sizeof(double)*M);
    MPI_Gatherv(local_x, recvcounts[rank], MPI_DOUBLE, x, recvcounts,offset,MPI_DOUBLE,0,MPI_COMM_WORLD);
    
	if(rank==0){
	double l1 = 0, l2=0, l_inf = 0;
	for(i = 0; i < M; i++){
	l1 += fabs(x[i] - 1);
	l2 += (x[i] - 1) * (x[i] - 1);
	if(l_inf < fabs(x[i] - 1))
	l_inf = fabs(x[i] - 1);
	}
	l2 = sqrt(l2);
	printf("l1 = %e, l1(l1/N) = %e, l2 = %e, l_inf = %e\n", l1, l1/M, l2, l_inf);
	}

    free(V);
    free(x);
    free(local_x);
    free(offset);
    free(recvcounts);
    for(i=0;i<rowCount;i++){
        free(A[i]);
    }
    free(A);
    MPI_Finalize();
    return(0);
}
