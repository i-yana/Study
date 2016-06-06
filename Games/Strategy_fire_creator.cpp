#include "Strategy_fire_creator.h"

namespace Games
{
	Strategy* Strategy_Fire_Creator::create_strategy() 
	{
		return new Strategy_Fire();
	}

	Strategy_Fire_Creator::Strategy_Fire_Creator() {}

	Strategy_Fire_Creator::~Strategy_Fire_Creator() {}
}