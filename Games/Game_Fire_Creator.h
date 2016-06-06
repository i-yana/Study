#ifndef GAME_FIRE_CREATOR_H
#define GAME_FIRE_CREATOR_H
#include "Factory_of_Games.h"
#include "Game_Fire.h"

namespace Games
{
	class Game_Fire_Creator : public Factory_of_Games
	{
	public:
		Game* create_game(const std::vector<std::string>& keys);
		Game_Fire_Creator();
		~Game_Fire_Creator();
	};
}
#endif