#include "Offline.h"
#include <iostream>
#include <fstream>

namespace Games
{
	const std::string EMPTY = "";

	Offline::Offline(const Arguments& arguments)
		: a(arguments) {}

	Offline::~Offline() {}

	std::string Offline::get_command() const
	{
		return EMPTY;
	}

	void Offline::get_help() const
	{

	}

	void Offline::play(Executor& executor) const
	{
		executor.tick_universe();
		executor.dump_file(a.out_file);
	}
}