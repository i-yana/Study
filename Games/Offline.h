#ifndef OFFLINE_MODE
#define OFFLINE_MODE
#include "Interface.h"

namespace Games
{
	class Offline: public Interface
	{
	public:
		Offline(const Arguments& arguments);
		~Offline();
		std::string get_command() const override;
		void get_help() const override;
		void play(Executor& executor) const;
		void print_universe(const Field<Cell>&, const size_t&) const override{};
	private:
		const Arguments& a;
	};
}
#endif