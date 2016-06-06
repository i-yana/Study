#include "Online.h"
#include <iostream>
#include <fstream>

namespace Games
{
	const std::string ABOUT_DUMP = "dump <filename> -сохранить вселенную в файл";
	const std::string ABOUT_TICK = "tick <n = 1>(и сокращенно t <n = 1>) - рассчитать n(по умолчанию 1) итераций и напечатать результат.";
	const std::string ABOUT_EXIT = "exit Ц завершить игру";
	const std::string ABOUT_HELP = "help Ц распечатать справку о командах";
	const std::string MARK_OF_UNIVERSE_NAME = "\t\t\t»м€ вселенной - ";
	const std::string MARK_OF_ITERATIONS = "\t\t\tЌомер итерации - ";
	const std::string EMPTY_MARK = " ";
	const std::string WALL_MARK = "X";
	const std::string HUMAN_MARK = "H";
	const std::string FIRE_MARK = "F";

	Online::Online(const Arguments& _arguments)
		: arguments(_arguments){}

	Online::~Online() {}

	std::string Online::get_command() const
	{
		string str;
		getline(std::cin, str);
		return str;
	}

	void Online::play(Executor& executor) const
	{
		while (executor.parse_command());
	}

	void Online::get_help() const
	{
		std::cout << ABOUT_DUMP << std::endl;
		std::cout << ABOUT_TICK << endl;
		cout << ABOUT_EXIT << endl;
		cout << ABOUT_HELP << endl << endl;

	}
	void Online::print_universe(const Field<Cell>& universe, const size_t& iteration) const 
	{
		cout << MARK_OF_UNIVERSE_NAME << universe.getName() << endl;
		cout << MARK_OF_ITERATIONS << iteration << endl;
		for (size_t i = 0; i < universe.getWidth(); i++)
		{
			for (size_t j = 0; j < universe.getHeight(); j++)
			{
				Type cell = universe.getCell(i, j).get_type();
				switch (cell)
				{
				case Games::CL_EMPTY:
					std::cout << EMPTY_MARK;
					break;
				case Games::CL_WALL:
					std::cout << WALL_MARK;
					break;
				case Games::CL_FIRE:
					std::cout << FIRE_MARK;
					break;
				case Games::CL_HUMAN:
					std::cout << HUMAN_MARK;
					break;
				default:
					break;
				}
			}
			std::cout << std::endl;
		}
	}
}