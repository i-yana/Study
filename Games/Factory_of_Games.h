#ifndef FACTORY_OF_GAMES_H
#define FACTORY_OF_GAMES_H
#include "Game.h"
#include <vector>
#include <string>

namespace Games
{
	class Factory_of_Games
	{
	public:
		virtual Game* create_game(const std::vector<std::string>& arguments) = 0;
		virtual ~Factory_of_Games() {}
	};
}

#endif