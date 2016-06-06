#include "Executor.h"
#include "Tools.h"


namespace Games
{
	const size_t ONE_TICK = 1;
	const size_t FIRST_SYMBOL = 0;
	const size_t FIRST_SYMBOL_OF_NAME_FILE = 5;
	const std::string HELP = "help";
	const std::string TICK = "tick";
	const std::string DUMP = "dump";
	const std::string REDUCE_TICK = "t";
	const std::string EXIT = "exit";
	const char TICK_FIRST_CHAR = 't';
	const char DELIM = ' ';

	const std::string NOT_CORRECT_COMMAND = "not correct command\n";
	const std::string NOT_CORRECT_ITERATIONS = "not correct iterations\n";

	Executor::Executor(const std::shared_ptr<Field<Cell>>& _universe, const std::shared_ptr<Interface>& _interface, size_t _iteration, const std::shared_ptr<Strategy>& _strategy)
		:	universe(_universe), 
			interface(_interface), 
			iteration(_iteration), 
			number_of_iterations(_iteration), 
			strategy(_strategy)
			{};

	Executor::~Executor(){};

	void Executor::tick_universe()
	{
		number_of_iterations += iteration;
		for (size_t i = 0; i < iteration; i++)
		{
			strategy->refresh_universe(*universe);
		}
		interface->print_universe(*universe, number_of_iterations);
	}

	bool Executor::parse_command()
	{
		string cmd;
		cmd = interface->get_command();
		if (cmd.empty())
		{
			return true;
		}
		if (cmd == HELP)
		{
			interface->get_help();
			return true;
		}
		if (cmd == EXIT)
		{
			return false;
		}
		if (cmd[FIRST_SYMBOL] == TICK_FIRST_CHAR)
		{
			size_t pos = cmd.find(DELIM);
			if (pos == cmd.npos)
			{
				if (cmd == REDUCE_TICK || cmd == TICK)
				{
					iteration = ONE_TICK;
					tick_universe();
				}
				else
				{
					cout << NOT_CORRECT_COMMAND;
				}
				return true;
			}
			else
			{
				string cut_cmd = cmd.substr(FIRST_SYMBOL, cmd.find(DELIM));
				if (cut_cmd == REDUCE_TICK || cut_cmd == TICK)
				{
					vector<int> count;
					if (!Tools::get_numbers(count, cmd))
					{
						cout << NOT_CORRECT_ITERATIONS;
						return true;
					}
					iteration = count[FIRST_SYMBOL];
					tick_universe();
					return true;
				}
				else
				{
					cout << NOT_CORRECT_COMMAND;
					return true;
				}
			}
		}
		string cut_cmd = cmd.substr(FIRST_SYMBOL, cmd.find(DELIM));
		if (cut_cmd == DUMP)
		{
			try
			{
				string file_name = cmd.substr(FIRST_SYMBOL_OF_NAME_FILE, cmd.size() - FIRST_SYMBOL_OF_NAME_FILE);
				dump_file(file_name);
			}
			catch (out_of_range&)
			{
				cout << NOT_CORRECT_COMMAND;
			}
			return true;
		}
		cout << NOT_CORRECT_COMMAND;
		return true;
	}

	void Executor::dump_file(const std::string& str) const
	{
		try
		{
			save->save_file(str, *universe, *strategy);
		}
		catch (const Exception::save_file& exp)
		{
			std::cerr << exp.what() << std::endl;
		}
	}
}