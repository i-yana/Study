#ifndef EXECUTOR_FIRE_H
#define EXECUTOR_FIRE_H
#include "Strategy.h"
#include "Interface.h"
#include "Save_File.h"
#include <memory>

namespace Games
{
	class Interface;

	class Executor
	{
	public:
		Executor(const std::shared_ptr<Field<Cell>>& universe, const std::shared_ptr<Interface>& interface, size_t iteration, const std::shared_ptr<Strategy>& strategy);
		~Executor();
		bool parse_command();
		void dump_file(const std::string&) const;
		void tick_universe();
		
	private:
		std::shared_ptr<Field<Cell>> universe;
		std::shared_ptr<Interface> interface;
		std::shared_ptr<Strategy> strategy;
		size_t iteration;
		size_t number_of_iterations;
		std::shared_ptr<Save> save;
	};
}

#endif