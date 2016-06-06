#ifndef STRATEGY_FIRE_H
#define STRATEGY_FIRE_H
#include "Strategy.h"

namespace Games
{
	class Strategy_Fire: public Strategy
	{
	public:
		Strategy_Fire(){};
		~Strategy_Fire();
		void refresh_universe(Field<Cell>& universe);
		std::string get_rules() const;
		void set_rules(std::string str);
	private:
		void reset_flags(Field<Cell>& universe);
		Cell& get_neighbor_of_cell(Field<Cell>& universe, const size_t& number, const size_t x, const size_t y);
	};
}
#endif