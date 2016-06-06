#include <mpi.h>
#include <omp.h>
#include <iostream>
#include <cstdlib>
#include <cmath>

double norm(double *vect, int N){
    double t = 0.0;
   
    #pragma omp parallel for reduction(+:t)
    for(size_t i = 0; i < N; ++i) {
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

  int N = 10;
  double time_s, time_e;

  int rowCount = N/size + (rank < N % size);
  double tau = 0.01;
  double eps = 0.00001;
  double norma = 0;

  double *A = new double[N*rowCount];
  double *x = new double[N];
  double *b = new double[N];

  int *recvcounts = new int[size];
  int *displs = new int[size];
  double localSum = 0;
  double globalSum = 0;
  double normaB = sqrt(N * (N + 1));
  double normaC;

  for (size_t i = 0; i < size; ++i) {
     recvcounts[i] = N/size + (i < N % size);
  }
  displs[0] = 0;

  for (size_t i = 1; i < size; ++i) {
      displs[i] = displs[i - 1] + recvcounts[i - 1];
  }
  
  for (size_t i = 0; i < rowCount; ++i){
      for(size_t j = 0; j < N; ++j){
          A[i * N + j] = (displs[rank] + i == j) ? 2.0 : 1.0;
      }
      b[i] = N + 1;
  }

  double *local = new double[N];
  double *tmp = new double[rowCount];
  time_s = MPI_Wtime();

  do{

      #pragma omp parallel for
      for(size_t i = 0; i < rowCount; ++i) {
          tmp[i] = 0;
          for(size_t j = 0; j < N; ++j) {
              tmp[i]+=A[i*N+j] * x[j];
          }
          tmp[i] -=b[i]; 
      }

      MPI_Allgatherv(tmp, rowCount, MPI_DOUBLE, local, recvcounts, displs, MPI_DOUBLE, MPI_COMM_WORLD);

      normaC = norm(local,N);

      #pragma omp parallel for
      for(size_t i = 0; i < N; ++i) {
          x[i]-=local[i]*tau;
          local[i]=0;
      }

    norma = normaC/normaB;

  } while(norma > eps);

  time_e = MPI_Wtime();  
    
	if(rank == 0){
  
      std::cout << time_e - time_s << std::endl;
   }
for(size_t i = 0; i < N; ++i) {
        std::cout << x[i] << " ";
    }
    std::cout << std::endl;
  }

  delete [] tmp;
  delete [] b;
  delete [] local;
  delete [] recvcounts;
  delete [] displs;
  delete [] A;
  delete [] x;

  MPI_Finalize();
}



#!/bin/sh

#PBS -l walltime=00:00:50
#PBS -l select=1:ncpus=8:mpiprocs=8:ompthreads=8:mem=2000m

cd $PBS_O_WORKDIR
mpirun -hostfile $PBS_NODEFILE ./main3.o