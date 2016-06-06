#ifndef GAME_LIFE_CREATOR_H
#define GAME_LIFE_CREATOR_H
#include "Factory_of_Games.h"
#include "Game_Life.h"

namespace Games
{
	class Game_Life_Creator : public Factory_of_Games
	{
	public:
		Game* create_game(const std::vector<std::string>& keys);
		Game_Life_Creator();
		~Game_Life_Creator();
	};
}
#endif