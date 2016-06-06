#ifndef GAME_FIRE_H
#define GAME_FIRE_H
#include "Executor.h"
#include "Parser.h"
#include "Game.h"
#include "Factory_of_Interfaces.h"

namespace Games
{
	class Game_Fire: public Game
	{
	public:
		Game_Fire(const std::vector<std::string>& _keys)
			:keys(_keys){};
		~Game_Fire();
		void run();
		void parse_argument(){};
	private:
		void select_interface();
		std::vector<std::string> keys;
		Parser_Fire::Parser_of_Arguments parser;
		Arguments arg;
		std::shared_ptr<Interface> interface;
		std::unique_ptr<Factory_of_Interfaces> factory_of_interfaces;
	};
}
#endif