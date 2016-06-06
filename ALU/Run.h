#ifndef TESTING_H
#define TESTING_H

#include <string>
#include <fstream>

namespace ALU 
{
    class Run 
	{
    public:
        static void start();
    private:
		static std::string getBitSequence(size_t, size_t);
    };
}

#endif 