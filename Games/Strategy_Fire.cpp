#include "Strategy_Fire.h"
#include <time.h>
namespace Games
{

	enum Neighbors
	{
		UPPER,
		RIGHT,
		LOWER,
		LEFT
	};

	const size_t NUMBER_OF_NEIGHBORS = 4;
	const size_t INITIAL_POSITION = 0;
	const size_t ONE = 1;


	Strategy_Fire::~Strategy_Fire() {}

	void Strategy_Fire::refresh_universe(Field<Cell>& universe)
	{
		reset_flags(universe);
		size_t width = universe.getWidth();
		size_t height = universe.getHeight();
		for (size_t i = INITIAL_POSITION; i < width; ++i)
		{
			for (size_t j = INITIAL_POSITION; j < height; ++j)
			{
				Cell& current_cell = universe.getCell(i, j);
				if (current_cell.get_flag())
				{
					continue;
				}
				Type cell_type = current_cell.get_type();

				if (cell_type == CL_EMPTY)
				{
					continue;
				}

				current_cell.set_flag(true);

				if (cell_type == CL_WALL)
				{
					continue;
				}

				if (cell_type == CL_FIRE)
				{
					size_t initial_iterator = std::rand() % NUMBER_OF_NEIGHBORS;
					for (size_t current_iterator = (initial_iterator + 1) % NUMBER_OF_NEIGHBORS; current_iterator != initial_iterator; current_iterator = (current_iterator + 1) % NUMBER_OF_NEIGHBORS)
					{
						Cell& neighbor = get_neighbor_of_cell(universe, current_iterator, i, j);
						Type neighbor_type = neighbor.get_type();
						if (neighbor_type == CL_WALL)
						{
							neighbor.set_flag(true);
							continue;
						}
						neighbor.set_type(CL_FIRE);
						neighbor.set_flag(true);
						break;
					}
				}
				if (cell_type == CL_HUMAN)
				{
					srand(time(NULL));
					size_t cur_nghbr = rand() % NUMBER_OF_NEIGHBORS;

					for (; cur_nghbr < NUMBER_OF_NEIGHBORS; ++cur_nghbr)
					{
						Cell& neighbor = get_neighbor_of_cell(universe, cur_nghbr, i, j);
						Type neighbor_type = neighbor.get_type();
						if (neighbor_type != CL_EMPTY)
						{
							continue;
						}
						else
						{
							neighbor.set_type(CL_HUMAN);
							neighbor.set_flag(true);
							current_cell.set_type(CL_EMPTY);
							break;
						}
					}
				}
			}
		}
	}

	void Strategy_Fire::reset_flags(Field<Cell>& universe)
	{
		size_t width = universe.getWidth();
		size_t height = universe.getHeight();
		for (size_t i = 0; i < width; ++i)
		{
			for (size_t j = 0; j < height; ++j)
			{
				universe.getCell(i, j).set_flag(false);
			}
		}
		return;
	}

	std::string Strategy_Fire::get_rules() const
	{
		return "";
	}

	void Strategy_Fire::set_rules(std::string str) {}

	Cell& Strategy_Fire::get_neighbor_of_cell(Field<Cell>& universe, const size_t& number, const size_t i, const size_t j)
	{
		size_t width = universe.getWidth();
		size_t height = universe.getHeight();
		const size_t SHIFT_UP = width - ONE;
		const size_t SHIFT_DOWN = width + ONE;
		const size_t SHIFT_TO_RIGHT = height + ONE;
		const size_t SHIFT_TO_LEFT = height - ONE;
		switch (number)
		{
		case UPPER:
			return universe.getCell((i + SHIFT_UP) % width, j);
		case RIGHT:
			return universe.getCell(i, (j + SHIFT_TO_RIGHT) % height);
		case LOWER:
			return universe.getCell((i + SHIFT_DOWN) % width, j);
		default:
			return universe.getCell(i, (j + SHIFT_TO_LEFT) % height);
		}
	}

}