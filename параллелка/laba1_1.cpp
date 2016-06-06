#include <mpi.h>
#include <omp.h>
#include <iostream>
#include <cstdlib>
#include <cmath>

double norm(double *vect, int n){
    double t = 0.0;
    for(size_t i = 0; i < n; ++i){
        t += vect[i]*vect[i];
    }
    t = sqrt(t);
    return t;
}

int main(int argc, char **argv){
     MPI_Init(&argc, &argv);
     int rank, size;
     MPI_Comm_rank(MPI_COMM_WORLD, &rank);
     MPI_Comm_size(MPI_COMM_WORLD, &size);
    
    int N = 6000;
    int rowCount = N / size + (rank < N % size);

    double tau = 0.01;
    double eps = 0.00001;
    double norma = 0;
    double time_s, time_e;
    double *A = new double[N * rowCount];
    double *x = new double[N];
    double *localX = new double[rowCount];
    double *b = new double[rowCount];
    
    int *recvcounts = new int[size];
    int *displs = new int[size];
    double localSum = 0;
    double globalSum = 0;
    double normaB = sqrt(N * (N + 1));

    for (size_t i = 0; i < size; ++i) {
        recvcounts[i] = N/size + (i < N % size);
    }
    displs[0] = 0;
    for (size_t i = 1; i < size; ++i) {
        displs[i] = displs[i - 1] + recvcounts[i - 1];
    }

    for (size_t i = 0; i < rowCount; ++i) {
        for (size_t j = 0; j < N; ++j) {
            A[i * N + j] = (displs[rank] + i == j) ? 2.0 : 1.0;
        }
        b[i] = N + 1;
    }

    double *tmp = new double[N];
    time_s = MPI_Wtime();
    do {
        localSum = 0;
        MPI_Allgatherv(localX, rowCount, MPI_DOUBLE, x, recvcounts, displs, MPI_DOUBLE, MPI_COMM_WORLD);

	#pragma omp parallel for
        for (size_t i = 0; i < rowCount; ++i) {
            tmp[i] = 0;
            for (size_t j = 0; j < N; ++j) {
                tmp[i] += A[i * N + j] * x[j];
            }
            tmp[i] -= b[i];
            localX[i] = localX[i] - tau * tmp[i];
            localSum += tmp[i]*tmp[i];
        }



        MPI_Allreduce(&localSum, &globalSum, 1, MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
        norma = sqrt(globalSum) / normaB;
    } while (norma > eps);

    time_e = MPI_Wtime();

    if (rank == 0) {
        std::cout << time_e - time_s << std::endl;
       // for (size_t i = 0; i < N; ++i) {
        //    std::cout << x[i] << " ";
       // }
       // std::cout << std::endl;
    }

    delete [] tmp;
    delete [] b;
    delete [] localX;
    delete [] recvcounts;
    delete [] displs;
    delete [] A;
    delete [] x;

    MPI_Finalize();
}