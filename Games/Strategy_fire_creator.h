#ifndef STRATEGY_FIRE_CREATOR_H
#define STRATEGY_FIRE_CREATOR_H
#include "Factory_of_Strategy.h"
#include "Strategy_Fire.h"

namespace Games
{
	class  Strategy_Fire_Creator : public Factory_of_Strategy
	{
	public:
		Strategy* create_strategy();
		Strategy* create_strategy(std::vector<int>, std::vector<int>) { return nullptr; };
		Strategy_Fire_Creator();
		~Strategy_Fire_Creator();
	};
}
#endif