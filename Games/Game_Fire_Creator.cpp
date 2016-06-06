#include "Game_Fire_Creator.h"

namespace Games
{
	Game* Game_Fire_Creator::create_game(const std::vector<std::string>& keys)
	{
		return new Game_Fire(keys);
	}

	Game_Fire_Creator::Game_Fire_Creator() {}

	Game_Fire_Creator::~Game_Fire_Creator() {}
}