#ifndef STRATEGY_LIFE_CREATOR_H
#define STRATEGY_LIFE_CREATOR_H
#include "Factory_of_Strategy.h"
#include "Strategy_Life.h"

namespace Games
{
	class  Strategy_Life_Creator : public Factory_of_Strategy
	{
	public:
		Strategy* create_strategy(std::vector<int>, std::vector<int>);
		Strategy* create_strategy(){
			return nullptr;
		};

		Strategy_Life_Creator();
		~Strategy_Life_Creator();
	};
}
#endif