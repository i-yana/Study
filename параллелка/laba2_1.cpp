#include <omp.h>
#include <iostream>
#include <cstdlib>
#include <cmath>
#define N 6000

int main(int argc, char **argv) {
  
    double tau = 0.01;
    double eps = 0.00001;
    double norma = 0;
    double normaC;
    double normaB = sqrt(N * (N + 1));
    double globalSum = 0;
    double A[N][N];
    double x[N] = {0};
    double b[N];
    
    #pragma omp parallel for   
    for (int i = 0; i < N; ++i){
        for(int j = 0; j < N; ++j){
            A[i][j] = (i == j) ? 2.0 : 1.0;
        }
        b[i] = N + 1;
    }

    double tmp[N];
    double time_s = omp_get_wtime();

    do{
        globalSum = 0;
        #pragma omp parallel for 
        for(int i = 0; i < N; ++i) {
            tmp[i] = 0;
	    for(int j = 0; j < N; ++j){
		tmp[i] += A[i][j] * x[j];	
	    }
        }

        #pragma omp parallel for reduction(+:globalSum)
        for(int i = 0; i < N; ++i){
            tmp[i] -= b[i];
            x[i] -= tmp[i] * tau;
	    globalSum += tmp[i]*tmp[i];
        }
        
        norma = sqrt(globalSum)/normaB;

    } while(eps<norma);
    double time_e = omp_get_wtime();
    std::cout << "Time = " << time_e - time_s << std::endl;
  for(size_t i = 0; i < N; ++i) {
       std::cout << x[i] << " ";
    }
}