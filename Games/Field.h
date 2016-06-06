#ifndef FIELD_H
#define FIELD_H
#include <stdexcept>
#include <vector>
#include <string>
#include <iostream>
using namespace std;
namespace Games
{
	namespace Exception
	{
		class coordinates_are_out_of_range : public std::exception {};

		class bad_universe_size : public std::exception {};
	}

	enum Type
	{
		CL_EMPTY = 0,
		CL_WALL,
		CL_FIRE,
		CL_HUMAN
	};

	class Cell
	{
	public:
		Cell(){
			has_change = false;
		};
		~Cell(){};
		bool get_flag() const
		{
			return has_change;
		}
		void set_flag(bool flag)
		{
			has_change = flag;
		}
		const Type& get_type() const
		{
			return cell;
		}

		void set_type(const Type& type)
		{
			cell = type;
		}

	private:
		Type cell;
		bool has_change;
	};

	template<class Cell>class Field
	{
	public:
		Field(const std::string& _name, const int& _width, const int& _height)
			:name(_name)
		{
			width = static_cast<size_t>(_width);
			height = static_cast<size_t>(_height);
			if (width < MIN_SIZE_WIDTH || height < MIN_SIZE_HEIGHT)
			{
				throw Exception::bad_universe_size();
			}
			field.resize(width);
			for (auto& item : field)
			{
				item.resize(height);
			}
			for (size_t i = 0; i < width; i++)
			{
				for (size_t j = 0; j < height; j++)
				{
					field[i][j].set_type(CL_EMPTY);
				}
			}
		}
		void setCell(const int& x, const int& y, Cell& cell)
		{
			if (x >= width || y >= height || x < MIN_INDEX_WIDTH || y < MIN_INDEX_HEIGHT)
			{
				throw Exception::coordinates_are_out_of_range();
			}
			field[x][y] = cell;
		}
		Cell& getCell(const int& x, const int& y)
		{
			if (x >= width || y >= height || x < MIN_INDEX_WIDTH || y < MIN_INDEX_HEIGHT)
			{
				throw Exception::coordinates_are_out_of_range();
			}
			return field[x][y];
		}
		const Cell& getCell(const int& x, const int& y) const
		{
			if (x >= width || y >= height || x < MIN_INDEX_WIDTH || y < MIN_INDEX_HEIGHT)
			{
				throw Exception::coordinates_are_out_of_range();
			}
			return field[x][y];
		}
		std::string getName() const
		{
			return name;
		}
		size_t getWidth() const
		{
			return width;
		}
		size_t getHeight() const
		{
			return height;
		}
		~Field(){};
	private:
		std::vector < std::vector< Cell > > field;
		size_t width;
		size_t height;
		std::string name;

		const size_t MIN_SIZE_WIDTH = 1;
		const size_t MIN_SIZE_HEIGHT = 1;
		const size_t MIN_INDEX_WIDTH = 0;
		const size_t MIN_INDEX_HEIGHT = 0;
	};
}
#endif