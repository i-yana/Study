#ifndef FACTORY_OF_INTERFACES_H
#define FACTORY_OF_INTERFACES_H
#include "Parser.h"
#include "Interface.h"

namespace Games
{
	class Factory_of_Interfaces
	{
	public:
		virtual Interface* create_interface(const Arguments& arguments) = 0;
		virtual ~Factory_of_Interfaces() {}
	};

}

#endif