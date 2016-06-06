#ifndef GAME_LIFE
#define GAME_LIFE
#include "Executor.h"
#include "Parser.h"
#include "Game.h"
#include "Factory_of_Interfaces.h"

namespace Games
{
	class Game_Life : public Game
	{
	public:
		Game_Life(const std::vector<std::string>& key)
		:keys(key){};
		~Game_Life();
		void run();
		void parse_argument(){};
	private:
		void select_interface();
		std::vector<std::string> keys;
		Parser_Life::Parser_of_Arguments parser;
		Arguments arg;
		std::shared_ptr<Interface> interface;
		std::unique_ptr<Factory_of_Interfaces> factory_of_interfaces;
		std::shared_ptr<Strategy> strategy;
	};
}
#endif