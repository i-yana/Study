#include "Game_Life_Creator.h"

namespace Games
{
	Game* Game_Life_Creator::create_game(const std::vector<std::string>& keys)
	{
		return new Game_Life(keys);
	}

	Game_Life_Creator::Game_Life_Creator() {}

	Game_Life_Creator::~Game_Life_Creator() {}
}