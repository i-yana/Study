#ifndef FACTORY_OF_STRATEGY_H
#define FACTORY_OF_STRATEGY_H
#include "Strategy.h"

namespace Games
{
	class Factory_of_Strategy
	{
	public:
		virtual Strategy* create_strategy() = 0;
		virtual Strategy* create_strategy(std::vector<int>, std::vector<int>) = 0;
		virtual ~Factory_of_Strategy() {}
	};
}

#endif