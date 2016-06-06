#include "Game_Run.h"
#include <iostream>
using namespace Games;

int main(int argc, char** argv)
{
	setlocale(LC_ALL, "Russian");
	try
	{
		Game_Run game(argc, argv);
		game.play();
	}
	catch (const Exception::not_enough_arguments& e)
	{
		std::cerr << e.what();
	}
	catch (const Exception::not_mark& e)
	{
		std::cerr << e.what();
	}
	catch (const Games::Parser_Fire::Exception::parse_arguments& e)
	{
		std::cerr << e.what();
	}

	catch (const Games::Parser_Fire::Exception::parse_input_file& e)
	{
		std::cerr << e.what();
	}

	catch (const Games::Parser_Life::Exception::parse_arguments& e)
	{
		std::cerr << e.what();
	}

	catch (const Games::Parser_Life::Exception::parse_input_file& e)
	{
		std::cerr << e.what();
	}
	return EXIT_SUCCESS;
}