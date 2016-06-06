#ifndef GAME_RUN_H
#define GAME_RUN_H
#include "Factory_of_Games.h"
#include "Parser.h"
#include <memory>

namespace Games
{
	const std::string NOT_MARK = "Not mark '-l' or '-f'";
	const std::string NOT_ENOUGTH_ARGUMENTS = "not enough arguments in command line ";

	class Game_Run
	{
	public:
		Game_Run(int _argc, char** _argv)
			:argc(_argc), argv(_argv){};
		~Game_Run();
		void play();
	private:
		void select_game();
		int argc;
		char** argv;
		std::shared_ptr<Game> game;
		std::unique_ptr<Factory_of_Games> factory_of_games;
	};

	namespace Exception
	{
		class parse_arguments : public std::exception
		{
		public:
			explicit parse_arguments(const std::string& why)
				: reason(why) {}
			const char* what() const throw()
			{
				return reason.c_str();
			}

		private:
			std::string reason;
		};
		class not_mark : public parse_arguments
		{
		public:
			explicit not_mark()
				: parse_arguments(NOT_MARK) {}
		};

		class not_enough_arguments : public parse_arguments
		{
		public:
			explicit not_enough_arguments()
				: parse_arguments(NOT_ENOUGTH_ARGUMENTS){}
		};
	}
}
#endif