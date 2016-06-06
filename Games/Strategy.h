#ifndef BASE_STRATEGY_H
#define BASE_STRATEGY_H
#include "Field.h"

namespace Games
{
	class Strategy
	{
	public:
		virtual void refresh_universe(Field<Cell>& universe) = 0;
		virtual std::string get_rules() const = 0;
		virtual void set_rules(std::string str) = 0;
		~Strategy(){};
	};
}
#endif