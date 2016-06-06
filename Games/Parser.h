#ifndef PARSER_H
#define PARSER_H
#include "Factory_of_Strategy.h"
#include <iostream>
#include <memory>
using namespace std;
namespace Games
{
	struct Arguments
	{
		string out_file = "";
		string in_file = "";
		size_t iteratons = 0;
		std::shared_ptr<Field<Cell>> universe;
		std::shared_ptr<Strategy> strategy;
	};

	namespace Parser_Fire
	{
		class Parser_of_Arguments
		{
		public:
			Parser_of_Arguments()
				:has_size(false),
				has_iterations(false)
				{};
			~Parser_of_Arguments(){};
			Arguments& parse_arguments(std::vector<std::string>& keys);
		private:
			void parse_input_file(const string& file_name);
			void parse_arg_line(std::vector<std::string>& keys);
			void parse_coordinate(const string& file_line);
			void parse_config(const string& file_line);
			string name_of_universe;
			int width;
			int height;
			bool has_size;
			bool has_iterations;
			Arguments a;
			std::unique_ptr<Factory_of_Strategy> factory;
		};

		namespace Exception
		{
			class parse_arguments : public std::exception
			{
			public:
				explicit parse_arguments(const std::string& why)
					: m_reason(why) {}
				const char* what() const throw()
				{
					return m_reason.c_str();
				}

			private:
				std::string m_reason;
			};
			class wrong_number_of_arguments : public parse_arguments
			{
			public:
				explicit wrong_number_of_arguments(const std::string& why)
					: parse_arguments("Wrong number of arguments" + why) {}
			};
			class redefiniton_of_argument : public parse_arguments
			{
			public:
				explicit redefiniton_of_argument(const std::string& why)
					: parse_arguments("Redefinition of argument number " + why) {}
			};
			class expected_number_of_iterations : public parse_arguments
			{
			public:
				explicit expected_number_of_iterations(const std::string& why)
					: parse_arguments("Wrong argument " + why + ", expected -i or --iterations=x") {}
			};
			class expected_output_file : public parse_arguments
			{
			public:
				explicit expected_output_file(const std::string& why)
					: parse_arguments("Wrong argument " + why + ", expected -o or --output=""file name""") {}
			};
			class wrong_number_of_iterations : public parse_arguments
			{
			public:
				explicit wrong_number_of_iterations(const std::string& why)
					: parse_arguments("Wrong number of iterations " + why) {}
			};
			class incorrect_output_file : public parse_arguments
			{
			public:
				explicit incorrect_output_file(const std::string& why)
					: parse_arguments("Incorrect the output file " + why) {}
			};


			class parse_input_file : public std::exception
			{
			public:
				explicit parse_input_file(const std::string& why)
					: m_reason(why) {}
				const char* what() const throw()
				{
					return m_reason.c_str();
				}

			private:
				std::string m_reason;
			};
			class not_found_version : public parse_input_file
			{
			public:
				explicit not_found_version(const std::string& why)
					: parse_input_file("Not found version " + why) {}
			};
			class cannot_open_input_file : public parse_input_file
			{
			public:
				explicit cannot_open_input_file(const std::string& why)
					: parse_input_file("Can not open input file: " + why) {}
			};
			class wrong_place_for_conditions : public parse_input_file
			{
			public:
				explicit wrong_place_for_conditions(const std::string& why)
					: parse_input_file("Wrong place for condition in line " + why) {}
			};

			class bad_configuration : public parse_input_file
			{
			public:
				explicit bad_configuration(const std::string& why)
					: parse_input_file("Incorrect configuration in line " + why) {}
			};
			class bad_coordinates : public parse_input_file
			{
			public:
				explicit bad_coordinates(const std::string& why)
					: parse_input_file("Incorrect coordinates in line " + why) {}
			};
			class bad_version : public parse_input_file
			{
			public:
				explicit bad_version(const std::string& why)
					: parse_input_file("Incorrect version in line " + why) {}
			};
			class bad_size : public parse_input_file
			{
			public:
				explicit bad_size(const std::string& why)
					: parse_input_file("Incorrect sizes in line " + why) {}
			};
			class bad_rules : public parse_input_file
			{
			public:
				explicit bad_rules(const std::string& why)
					: parse_input_file("Incorrect rules in line " + why) {}
			};
			class redefinition_of_configuration : public parse_input_file
			{
			public:
				explicit redefinition_of_configuration(const std::string& why)
					: parse_input_file("Redifinition of configuration in line " + why) {}
			};
		}
	}



	namespace Parser_Life
	{
		class Parser_of_Arguments
		{
		public:
			Parser_of_Arguments()
				:has_size(false), 
				has_iterations(false),
				has_rules(false){};
			~Parser_of_Arguments(){};
			Arguments& parse_arguments(std::vector<std::string>& keys);
		private:
			void parse_input_file(const string& file_name);
			void parse_arg_line(std::vector<std::string>& keys);
			void parse_coordinate(const string& file_line);
			void parse_config(const string& file_line);
			string name_of_universe;
			int width;
			int height;
			bool has_size;
			bool has_iterations;
			bool has_rules;
			Arguments a;
			std::unique_ptr<Factory_of_Strategy> factory;
		};
		namespace Exception
		{
			class parse_arguments : public std::exception
			{
			public:
				explicit parse_arguments(const std::string& why)
					: m_reason(why) {}
				const char* what() const throw()
				{
					return m_reason.c_str();
				}

			private:
				std::string m_reason;
			};
			class wrong_number_of_arguments : public parse_arguments
			{
			public:
				explicit wrong_number_of_arguments(const std::string& why)
					: parse_arguments("Wrong number of arguments " + why) {}
			};
			class redefiniton_of_argument : public parse_arguments
			{
			public:
				explicit redefiniton_of_argument(const std::string& why)
					: parse_arguments("Redefinition of argument number" + why) {}
			};
			class expected_number_of_iterations : public parse_arguments
			{
			public:
				explicit expected_number_of_iterations(const std::string& why)
					: parse_arguments("Wrong argument " + why + ", expected -i or --iterations=x") {}
			};
			class expected_output_file : public parse_arguments
			{
			public:
				explicit expected_output_file(const std::string& why)
					: parse_arguments("Wrong argument " + why + ", expected -o or --output=""file name""") {}
			};
			class wrong_number_of_iterations : public parse_arguments
			{
			public:
				explicit wrong_number_of_iterations(const std::string& why)
					: parse_arguments("Wrong number of iterations " + why) {}
			};
			class incorrect_output_file : public parse_arguments
			{
			public:
				explicit incorrect_output_file(const std::string& why)
					: parse_arguments("Incorrect the output file  " + why) {}
			};
			
			class parse_input_file : public std::exception
			{
			public:
				explicit parse_input_file(const std::string& why)
					: m_reason(why) {}
				const char* what() const throw()
				{
					return m_reason.c_str();
				}

			private:
				std::string m_reason;
			};
			class not_found_version : public parse_input_file
			{
			public:
				explicit not_found_version(const std::string& why)
					: parse_input_file("Not found version " + why) {}
			};
			class cannot_open_input_file : public parse_input_file
			{
			public:
				explicit cannot_open_input_file(const std::string& why)
					: parse_input_file("Can not open input file: " + why) {}
			};
			class wrong_place_for_conditions : public parse_input_file
			{
			public:
				explicit wrong_place_for_conditions(const std::string& why)
					: parse_input_file("Wrong place for condition in line " + why) {}
			};

			class bad_configuration : public parse_input_file
			{
			public:
				explicit bad_configuration(const std::string& why)
					: parse_input_file("Incorrect configuration in line " + why) {}
			};
			class bad_coordinates : public parse_input_file
			{
			public:
				explicit bad_coordinates(const std::string& why)
					: parse_input_file("Incorrect coordinates in line " + why) {}
			};
			class bad_version : public parse_input_file
			{
			public:
				explicit bad_version(const std::string& why)
					: parse_input_file("Incorrect version in line " + why) {}
			};
			class bad_size : public parse_input_file
			{
			public:
				explicit bad_size(const std::string& why)
					: parse_input_file("Incorrect sizes in line " + why) {}
			};
			class bad_rules : public parse_input_file
			{
			public:
				explicit bad_rules(const std::string& why)
					: parse_input_file("Incorrect rules in line " + why) {}
			};
			class redefinition_of_configuration : public parse_input_file
			{
			public:
				explicit redefinition_of_configuration(const std::string& why)
					: parse_input_file("Redifinition of configuration in line " + why) {}
			};
		}
	}

}
#endif