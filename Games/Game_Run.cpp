#include "Game_Run.h"
#include "Game_Life_Creator.h"
#include "Game_Fire_Creator.h"
#include <iostream>
#include <vector>

namespace Games
{
	const std::string LIFE_MARK = "-l";
	const std::string FIRE_MARK = "-f";
	const size_t FIRST_SYMBOL = 0;
	const std::string EMPTY = "";
	const size_t ONE_ARGUMENT = 1;

	void Game_Run::play()
	{
		select_game();
		game->parse_argument();
		game->run();
	}

	void Game_Run::select_game()
	{
		std::vector<std::string> keys;
		if (argc == ONE_ARGUMENT)
		{
			throw Exception::not_enough_arguments();
		}
		for (int i = 1; i < argc; i++)
		{
			keys.push_back(argv[i]);
		}
		if (keys[FIRST_SYMBOL] == LIFE_MARK)
		{
			factory_of_games.reset(new Game_Life_Creator);
			game.reset(factory_of_games->create_game(keys));
			return;
		}
		if (keys[FIRST_SYMBOL] == FIRE_MARK)
		{
			factory_of_games.reset(new Game_Fire_Creator);
			game.reset(factory_of_games->create_game(keys));
			return;
		}
		throw Exception::not_mark();
	}
	Game_Run::~Game_Run()
	{}
}