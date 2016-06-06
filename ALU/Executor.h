#ifndef EXECUTOR_H
#define EXECUTOR_H

#include "Scheme.h"
#include "Item.h"
#include <vector>

namespace ALU 
{	
    class Executor 
	{
    public:
        Executor(const Scheme&);
		bitArray calculate(std::string);
        std::string showTitleString();
    private:
        Scheme m_scheme;
        void setInputs(std::string);
        void clearInputs();
    };
}

#endif