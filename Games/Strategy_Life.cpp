#include "Strategy_Life.h"

namespace Games
{
	const int MIN_VALUE_OF_RULE = 0;
	const int MAX_VALUE_OF_RULE = 8;
	const int SIZE_OF_RULES = 9;

	Strategy_Life::Strategy_Life(std::vector<int> v1, std::vector<int> v2)
		: m_conditions_of_birth(SIZE_OF_RULES, CL_EMPTY), m_conditions_of_survival(SIZE_OF_RULES, CL_EMPTY)
	{
		for (const auto i : v1)
		{
			if (i < MIN_VALUE_OF_RULE || i > MAX_VALUE_OF_RULE)
			{
				throw Exception::bad_value_of_rules();
			}
			else
			{
				m_conditions_of_birth[i] = CL_WALL;
			}
		}
		for (const auto i : v2)
		{
			if (i < MIN_VALUE_OF_RULE || i > MAX_VALUE_OF_RULE)
			{
				throw Exception::bad_value_of_rules();
			}
			else
			{
				m_conditions_of_survival[i] = CL_WALL;
			}
		}
	}

	Strategy_Life::~Strategy_Life() {}

	std::string Strategy_Life::get_rules() const
	{
		return rules;
	}

	void Strategy_Life::set_rules(std::string str)
	{
		rules = str;
	}

	void Strategy_Life::refresh_universe(Field<Cell>& universe)
	{
		size_t width = universe.getWidth();
		size_t height = universe.getHeight();
		for (size_t i = 0; i < width; ++i)
		{
			for (size_t j = 0; j < height; ++j)
			{
				determine_state_of_cell(universe, i, j);
			}
		}
	}

	size_t Strategy_Life::count_number_of_neighbors(Field<Cell>& universe, size_t i, size_t j)
	{
		size_t number=0;
		size_t width = universe.getWidth();
		size_t height = universe.getHeight();
		const size_t SHIFT_UP = width - 1;
		const size_t SHIFT_DOWN = width + 1;
		const size_t SHIFT_TO_RIGHT = height + 1;
		const size_t SHIFT_TO_LEFT = height - 1;

		if (universe.getCell((i + SHIFT_UP) % width, (j + SHIFT_TO_LEFT) % height).get_type())
		{
			++number;
		}
		if (universe.getCell((i + SHIFT_UP) % width, j).get_type())
		{
			++number;
		}
		if (universe.getCell((i + SHIFT_UP) % width, (j + SHIFT_TO_RIGHT) % height).get_type())
		{
			++number;
		}
		if (universe.getCell(i, (j + SHIFT_TO_LEFT) % height).get_type())
		{
			++number;
		}
		if (universe.getCell(i, (j + SHIFT_TO_RIGHT) % height).get_type())
		{
			++number;
		}
		if (universe.getCell((i + SHIFT_DOWN) % width, (j + SHIFT_TO_LEFT) % height).get_type())
		{
			++number;
		}
		if (universe.getCell((i + SHIFT_DOWN) % width, j).get_type())
		{
			++number;
		}
		if (universe.getCell((i + SHIFT_DOWN) % width, (j + SHIFT_TO_RIGHT) % height).get_type())
		{
			++number;
		}
		return number;
	}

	void Strategy_Life::determine_state_of_cell(Field<Cell>& universe, const int& x, const int& y)
	{
		Cell& cell = universe.getCell(x, y);
		if (cell.get_type())
		{
			cell.set_type(m_conditions_of_survival[count_number_of_neighbors(universe,x,y)]);
		}
		else
		{
			cell.set_type(m_conditions_of_birth[count_number_of_neighbors(universe,x,y)]);
		}
	}

	const std::vector<Type>& Strategy_Life::get_conditions_of_birth() const
	{
		return m_conditions_of_birth;
	}

	const std::vector<Type>& Strategy_Life::get_conditions_of_survival() const
	{
		return m_conditions_of_survival;
	}

}