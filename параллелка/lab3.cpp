#include <mpi.h>
#include <omp.h>
#include <iostream>
#include <cstdlib>
#include <cmath>

double norm(double *vect, int N){
    double t = 0.0;
   
    #pragma omp parallel for reduction(+:t)
    for(int i = 0; i < N; ++i) {
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

  int N = 10000;
  double time1, time2;

  int rowCount = N/size + (rank < N % size);
  double tau = 0.0001;
  double eps = 0.0000001;
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

  for (int i = 0; i < size; ++i) {
     recvcounts[i] = N/size + (i < N % size);
  }
  displs[0] = 0;

  for (int i = 1; i < size; ++i) {
      displs[i] = displs[i - 1] + recvcounts[i - 1];
  }
  
  for (int i = 0; i < rowCount; ++i){
      for(int j = 0; j < N; ++j){
          A[i * N + j] = (displs[rank] + i == j) ? 2.0 : 1.0;
      }
      b[i] = N + 1;
  }

  double *local = new double[N];
  double *tmp = new double[rowCount];
  time1 = MPI_Wtime();

  do{

      #pragma omp parallel for
      for(int i = 0; i < rowCount; ++i) {
          tmp[i] = 0;
          for(int j = 0; j < N; ++j) {
              tmp[i]+=A[i*N+j] * x[j];
          }
          tmp[i] -=b[i]; 
      }

      MPI_Allgatherv(tmp, rowCount, MPI_DOUBLE, local, recvcounts, displs, MPI_DOUBLE, MPI_COMM_WORLD);

      normaC = norm(local,N);

      #pragma omp parallel for
      for(int i = 0; i < N; ++i) {
          x[i]-=local[i]*tau;
          local[i]=0;
      }

    norma = normaC/normaB;

  } while(norma > eps);

  time2 = MPI_Wtime();  
    
  std::cout<< "Rank " << rank << " Time: " << time2 - time1 << std::endl;
 	MPI_Barrier(MPI_COMM_WORLD);
  if(rank==0){
	double l1 = 0, l2=0, l_inf = 0;
	for(int i = 0; i < N; i++){
	l1 += fabs(x[i] - 1);
	l2 += (x[i] - 1) * (x[i] - 1);
	if(l_inf < fabs(x[i] - 1));
	l_inf = fabs(x[i] - 1);
	}
	l2 = sqrt(l2);
	printf("l1 = %e, l1(MAE) = %e, l2 = %e, l_inf = %e\n", l1, l1/N, l2, l_inf);

}
  //for(int i = 0; i < N; ++i) {
  //      std::cout << x[i] << " ";
  //}
  //  std::cout << std::endl;
  //}

  delete [] tmp;
  delete [] b;
  delete [] local;
  delete [] recvcounts;
  delete [] displs;
  delete [] A;
  delete [] x;

  MPI_Finalize();
  return 0;	
}