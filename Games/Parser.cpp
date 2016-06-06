#include "Strategy_fire_creator.h"
#include "Strategy_life_creator.h"
#include "Parser.h"
#include "Tools.h"
#include <fstream>
#include <iostream>

namespace Games
{
	const size_t FIRST_COORD = 0;
	const size_t SECOND_COORD = 1;
	const size_t ONE_SYMBOL = 1;
	const size_t FIRST_ARGUMENT = 1;
	const size_t TWO_ARGUMENTS = 2;
	const size_t FIRST_SYMBOL = 0;
	const size_t INITIAL_WIDTH = 6;
	const size_t INITIAL_HEIGHT = 6;
	const size_t LENGTH_MARK_OF_ITERATION = 13;
	const size_t LENGTH_MARK_OF_OUT_FILE = 9;
	const size_t FIRST_SYMBOR_OF_NAME = 3;
	const std::string EMPTY = "";
	const std::string INITIAL_RULES = "#R B3/S23";
	const std::string DEFAULT_NAME = "Default name";
	const std::string NOT_SIZE = "not size\n";
	const std::string EXPECTED_VERSION = " expected version\n";
	const std::string REDUSE_MARK_ITERATION = "-i";
	const std::string MARK_ITERATION = "--iterations";
	const std::string MARK_OF_FIRE = "#Fire";
	const std::string LIFE_VERSION = "#Life 1.06";
	const std::string REDUSE_MARK_OUT_FILE = "-o";
	const std::string MARK_OUT_FILE = "--output=";
	const std::string INITIAL_INPUT_FILE = "fire.txt";
	const std::string INITIAL_IN_FILE_LIFE = "life.txt";
	const char SHARP = '#';
	const char WALL = 'W';
	const char HUMAN = 'H';
	const char FIRE = 'F';
	const char NAME_MARK = 'N';
	const char SIZE_MARK = 'S';
	const char RULE_MARK = 'R';

	namespace Parser_Fire
	{
		Arguments& Parser_of_Arguments::parse_arguments(std::vector<std::string>& key)
		{
			if (key.size() == TWO_ARGUMENTS)
			{
				parse_input_file(key[FIRST_ARGUMENT]);
				return a;
			}
			if (key.size() > TWO_ARGUMENTS)
			{
				parse_arg_line(key);
				return a;
			}
			throw Exception::wrong_number_of_arguments("");
		}

		void Parser_of_Arguments::parse_input_file(const string& file_name)
		{
			a.in_file = file_name;
			std::ifstream input_file(a.in_file);
			if (!input_file)
			{
				throw Exception::cannot_open_input_file(a.in_file);
			}
			string file_line;
			string coordinate_line;
			getline(input_file, file_line);
			if (file_line == MARK_OF_FIRE)
			{
				while (input_file.good())
				{
					getline(input_file, file_line);
					if (file_line.empty())
					{
						continue;
					}
					if (SHARP != file_line[FIRST_SYMBOL])
					{
						coordinate_line = file_line;
						break;
					}
					parse_config(file_line);
				}
				if (name_of_universe.empty())
				{
					name_of_universe = DEFAULT_NAME;
				}
				if (!has_size)
				{
					std::cout << NOT_SIZE;
					width = INITIAL_WIDTH;
					height = INITIAL_HEIGHT;
				}
				try
				{
					a.universe.reset(new Field<Cell>(name_of_universe, width, height));
				}
				catch (const Games::Exception::bad_universe_size&)
				{
					throw Exception::bad_size("");
				}
				factory.reset(new Strategy_Fire_Creator);
				a.strategy.reset(factory->create_strategy());
				
				if (!coordinate_line.empty())
				{
					parse_coordinate(coordinate_line);
				}
				while (input_file.good())
				{
					getline(input_file, coordinate_line);
					if (coordinate_line.empty())
					{
						continue;
					}
					parse_coordinate(coordinate_line);
				}
			}
			else
			{
				string expected = EXPECTED_VERSION;
				throw Exception::not_found_version(file_line + expected);
			}
		}

		void Parser_of_Arguments::parse_arg_line(std::vector<std::string>& str)
		{
			for (size_t i = 1; i < str.size(); i++)
			{
				if (!has_iterations)
				{
					if (str[i] == REDUSE_MARK_ITERATION && !str[i + ONE_SYMBOL].empty())
					{
						try
						{
							a.iteratons = stoi(str[++i]);
						}
						catch (const std::invalid_argument&)
						{
							throw Exception::wrong_number_of_iterations(str[i]);
						}
						has_iterations = true;
						continue;
					}
					string tmp = str[i].substr(FIRST_SYMBOL, LENGTH_MARK_OF_ITERATION);
					if (MARK_ITERATION == tmp)
					{
						string num = str[i].substr(LENGTH_MARK_OF_ITERATION, str[i].size() - ONE_SYMBOL);
						try
						{
							a.iteratons = stoi(num);
						}
						catch (const std::invalid_argument&)
						{
							throw Exception::wrong_number_of_iterations(str[i]);
						}
						has_iterations = true;
						continue;
					}
					throw Exception::expected_number_of_iterations("");
				}
				if (a.out_file.empty())
				{
					if (str[i] == REDUSE_MARK_OUT_FILE && !str[i + ONE_SYMBOL].empty())
					{
						a.out_file = str[++i];
						continue;
					}
					string tmp = str[i].substr(FIRST_SYMBOL, LENGTH_MARK_OF_OUT_FILE);
					if (MARK_OUT_FILE == tmp)
					{
						a.out_file = str[i].substr(LENGTH_MARK_OF_OUT_FILE, str[i].size() - ONE_SYMBOL);
						continue;
					}
					throw Exception::expected_output_file(str[i]);
				}
				throw Exception::wrong_number_of_arguments("");
			}
			std::ifstream output_file(a.out_file);
			if (!output_file)
			{
				throw Exception::incorrect_output_file(a.out_file);
			}
			parse_input_file(INITIAL_INPUT_FILE);
		}

		void Parser_of_Arguments::parse_coordinate(const string& coordinate_line)
		{
			int x;
			int y;
			vector<int> numbers;
			if (!Tools::get_numbers(numbers, coordinate_line))
			{
				throw Exception::bad_coordinates(coordinate_line);
			}
			x = numbers[FIRST_COORD];
			y = numbers[SECOND_COORD];
			switch (coordinate_line[FIRST_SYMBOL])
			{
			case WALL:
			{
				a.universe->getCell(x, y).set_type(CL_WALL);
				break;
			}
			case FIRE:
			{
				a.universe->getCell(x, y).set_type(CL_FIRE);
				break;
			}
			case HUMAN:
			{
				a.universe->getCell(x, y).set_type(CL_HUMAN);
				break;
			}
			default:
			{
				throw Exception::bad_coordinates(coordinate_line);
			}
			break;
			}
		}

		void Parser_of_Arguments::parse_config(const string& file_line)
		{
			if (file_line[FIRST_ARGUMENT] == NAME_MARK)
			{
				if (!name_of_universe.empty())
				{
					throw Exception::redefinition_of_configuration(file_line);
				}
				name_of_universe = file_line.substr(3, file_line.size() - 3);
				return;
			}
			if (file_line[FIRST_ARGUMENT] == SIZE_MARK)
			{
				if (has_size)
				{
					throw Exception::redefinition_of_configuration(file_line);
				}
				vector<int> numbers;
				Tools::get_numbers(numbers, file_line);
				width = numbers[0];
				height = numbers[1];
				has_size = true;
				return;
			}
			throw Exception::bad_configuration(file_line);
		}
	}
	
	namespace Parser_Life
	{
		const std::vector<int> INITIAL_CONDITIONS_OF_BIRTH = { 3 };
		const std::vector<int> INITIAL_CONDITIONS_OF_SURVIVAL = { 2, 3 };

		Arguments& Parser_of_Arguments::parse_arguments(std::vector<std::string>& key)
		{
			if (key.size() == TWO_ARGUMENTS)
			{
				parse_input_file(key[FIRST_ARGUMENT]);
				return a;
			}
			if (key.size() > TWO_ARGUMENTS)
			{
				parse_arg_line(key);
				return a;
			}
			throw Exception::wrong_number_of_arguments(EMPTY);
		}
		
		void Parser_of_Arguments::parse_input_file(const string& file_name)
		{
			a.in_file = file_name;
			std::ifstream input_file(a.in_file);
			if (!input_file)
			{
				throw Exception::cannot_open_input_file(a.in_file);
			}
			string file_line;
			string coordinate_line;
			getline(input_file, file_line);
			if (file_line == LIFE_VERSION)
			{
				while (input_file.good())
				{
					getline(input_file, file_line);
					if (file_line.empty())
					{
						continue;
					}
					if (SHARP != file_line[FIRST_SYMBOL])
					{
						coordinate_line = file_line;
						break;
					}
					parse_config(file_line);
				}
				if (name_of_universe.empty())
				{
					name_of_universe = DEFAULT_NAME;
				}
				if (!has_size)
				{
					std::cout << NOT_SIZE;
					width = INITIAL_WIDTH;
					height = INITIAL_HEIGHT;
				}
				if (!has_rules)
				{
					factory.reset(new Strategy_Life_Creator);
					a.strategy.reset(factory->create_strategy(INITIAL_CONDITIONS_OF_BIRTH,INITIAL_CONDITIONS_OF_SURVIVAL));
					a.strategy->set_rules(INITIAL_RULES);
				}

				try
				{
					a.universe.reset(new Field<Cell>(name_of_universe, width, height));
				}
				catch (const Games::Exception::bad_universe_size&)
				{
					throw Exception::bad_size(EMPTY);
				}
				if (!coordinate_line.empty())
				{
					parse_coordinate(coordinate_line);
				}
				while (input_file.good())
				{
					getline(input_file, coordinate_line);
					if (coordinate_line.empty())
					{
						continue;
					}
					parse_coordinate(coordinate_line);
				}
			}
			else
			{
				throw Exception::not_found_version(file_line + EXPECTED_VERSION);
			}
		}
		
		void Parser_of_Arguments::parse_arg_line(std::vector<std::string>& str)
		{
			for (size_t i = 1; i < str.size(); i++)
			{
				if (!has_iterations)
				{
					if (str[i] == REDUSE_MARK_ITERATION && !str[i + ONE_SYMBOL].empty())
					{
						try
						{
							a.iteratons = stoi(str[++i]);
						}
						catch (const std::invalid_argument&)
						{
							throw Exception::wrong_number_of_iterations(str[i]);
						}
						has_iterations = true;
						continue;
					}
					string tmp = str[i].substr(FIRST_SYMBOL, LENGTH_MARK_OF_ITERATION);
					if (MARK_ITERATION == tmp)
					{
						string num = str[i].substr(LENGTH_MARK_OF_ITERATION, str[i].size() - ONE_SYMBOL);
						try
						{
							a.iteratons = stoi(num);
						}
						catch (const std::invalid_argument&)
						{
							throw Exception::wrong_number_of_iterations(str[i]);
						}
						has_iterations = true;
						continue;
					}
					throw Exception::expected_number_of_iterations("");
				}
				if (a.out_file.empty())
				{
					if (str[i] == REDUSE_MARK_OUT_FILE && !str[i + ONE_SYMBOL].empty())
					{
						a.out_file = str[++i];
						continue;
					}
					string tmp = str[i].substr(FIRST_SYMBOL, LENGTH_MARK_OF_OUT_FILE);
					if (MARK_OUT_FILE == tmp)
					{
						a.out_file = str[i].substr(LENGTH_MARK_OF_OUT_FILE, str[i].size() - ONE_SYMBOL);
						continue;
					}
					throw Exception::expected_output_file(str[i]);
				}
				throw Exception::wrong_number_of_arguments("");
			}
			std::ifstream output_file(a.out_file);
			if (!output_file)
			{
				throw Exception::incorrect_output_file(a.out_file);
			}
			parse_input_file(INITIAL_IN_FILE_LIFE);
		}

		void Parser_of_Arguments::parse_coordinate(const string& coordinate_line)
		{
			int x;
			int y;
			vector<int> numbers;
			if (!Tools::get_numbers(numbers, coordinate_line))
			{
				throw Exception::bad_coordinates(coordinate_line);
			}
			x = numbers[FIRST_COORD];
			y = numbers[SECOND_COORD];
			a.universe->getCell(x, y).set_type(CL_WALL);
		}
		
		void Parser_of_Arguments::parse_config(const string& file_line)
		{
			if (file_line[FIRST_ARGUMENT] == NAME_MARK)
			{
				if (!name_of_universe.empty())
				{
					throw Exception::redefinition_of_configuration(file_line);
				}
				name_of_universe = file_line.substr(FIRST_SYMBOR_OF_NAME, file_line.size() - FIRST_SYMBOR_OF_NAME);
				return;
			}
			
			if (file_line[FIRST_ARGUMENT] == RULE_MARK)
			{
				if (has_rules)
				{
					throw Exception::redefinition_of_configuration(file_line);
				}
				vector<int> birth;
				vector<int> survival;
				std::string b_str = file_line.substr(file_line.find('B'), file_line.find('/') - file_line.find('B'));
				std::string s_str = file_line.substr(file_line.find('S'), file_line.size() - file_line.find('S'));
				if (!Tools::get_numbers(birth, b_str))
				{
					throw Exception::bad_coordinates(file_line);
				}
				if (!Tools::get_numbers(survival, s_str))
				{
					throw Exception::bad_coordinates(file_line);
				}
				factory.reset(new Strategy_Life_Creator);
				a.strategy.reset(factory->create_strategy(INITIAL_CONDITIONS_OF_BIRTH, INITIAL_CONDITIONS_OF_SURVIVAL));
				a.strategy->set_rules(file_line);
				has_rules = true;
				return;
			}

			if (file_line[FIRST_ARGUMENT] == SIZE_MARK)
			{
				if (has_size)
				{
					throw Exception::redefinition_of_configuration(file_line);
				}
				vector<int> numbers;
				Tools::get_numbers(numbers, file_line);
				width = numbers[FIRST_COORD];
				height = numbers[SECOND_COORD];
				has_size = true;
				return;
			}
			throw Exception::bad_configuration(file_line);
		}

	}
}