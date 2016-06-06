#include "Exeption.h"
#include "Run.h"
#include <iostream>

using namespace ALU;

int main()
{
	try
	{
		Run::start();
	}
	catch (const AluException::ALUExeption& e)
	{
		std::cerr << e.what();
	}
    return EXIT_SUCCESS;
}
