#include "Run.h"
#include "Executor.h"
#include "Scheme.h"

#include <iostream>
#include <math.h>


namespace ALU
{
	const size_t TRUE = 1;
	const size_t FALSE = 0;
	const size_t two = 2;
	const size_t OPERATION_BITS = 1;
	const size_t OUT_FILE = 0;
	const std::string PATH = "ALU.txt";
	const std::string outputFiles[4][2] = { { "xor.txt", "00" }, { "and.txt", "01" }, { "or.txt", "10" }, { "sum.txt", "11" } };


	std::string Run::getBitSequence(size_t a, size_t lengthInputBits)
	{
		std::string resultSequence;
		while (lengthInputBits--)
		{
			resultSequence = std::to_string(a % two) + resultSequence;
			a /= two;
		}
		return resultSequence;
	}

	void Run::start()
	{
		Scheme scheme(PATH);
		Executor exec(scheme);

		for (auto t : outputFiles)
		{
			std::ofstream file;
			file.open((t[OUT_FILE]));
			file << exec.showTitleString() << std::endl;
			size_t lenSequence = scheme.inputs.size() - two;
			size_t TableTruthLines = pow(two, lenSequence);

			for (int i = 0; i < TableTruthLines; ++i)
			{
				std::string inBits = getBitSequence(i, lenSequence) + t[OPERATION_BITS];
				bitArray resultBitSequence = exec.calculate(inBits);

				for (auto inBit : inBits)
					file << inBit << TAB;
				for (bool bit : resultBitSequence)
				{
					file << (bit ? TRUE : FALSE) << TAB;
				}
				file << std::endl;
			}
			file.close();
		}
	}
}