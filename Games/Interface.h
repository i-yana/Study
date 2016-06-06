#ifndef INTERFACE_H
#define INTERFACE_H
#include "Executor.h"
#include "Parser.h"

namespace Games
{
	class Executor;

	class Interface
	{
	public:
		virtual std::string get_command() const = 0;
		virtual void get_help() const{};
		virtual ~Interface() {};
		virtual void play(Executor& executor) const = 0;
		virtual void print_universe(const Field<Cell>& universe, const size_t& iteration) const = 0;
	};
}
#endif
