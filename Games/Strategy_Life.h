#ifndef STRATEGY_LIFE_H
#define STRATEGY_LIFE_H
#include "Strategy.h"
#include <string>

namespace Games
{
	class Strategy_Life:public Strategy
	{
	public:
		Strategy_Life(std::vector<int> v1, std::vector<int> v2);
		~Strategy_Life();
		void refresh_universe(Field<Cell>& universe);
		const std::vector<Type>& get_conditions_of_birth() const;
		const std::vector<Type>& get_conditions_of_survival() const;
		std::string get_rules() const;
		void set_rules(std::string str);
	private:
		size_t count_number_of_neighbors(Field<Cell>& universe, size_t x, size_t y);
		void determine_state_of_cell(Field<Cell>& universe, const int& x, const int& y);
		std::vector<Type> m_conditions_of_birth;
		std::vector<Type> m_conditions_of_survival;
		std::string rules;
	};

	namespace Exception
	{
		class bad_value_of_rules : public std::exception {};
	}
}

#endif