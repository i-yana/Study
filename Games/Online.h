#ifndef ONLINE_H
#define ONLINE_H
#include "Interface.h"

namespace Games
{
	class Online : public Interface
	{
	public:
		Online(const Arguments& arguments);
		~Online();
		std::string get_command() const override;
		void get_help() const override;
		void play(Executor& executor) const;
		void print_universe(const Field<Cell>& universe, const size_t& iteration) const override;
	private:
		const Arguments& arguments;
	};
}
#endif