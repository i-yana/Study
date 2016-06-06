#include "Strategy_life_creator.h"

namespace Games
{
	Strategy* Strategy_Life_Creator::create_strategy(std::vector<int> birth, std::vector<int> survival)
	{
		return new Strategy_Life(birth, survival);
	}

	Strategy_Life_Creator::Strategy_Life_Creator() {}

	Strategy_Life_Creator::~Strategy_Life_Creator() {}
}