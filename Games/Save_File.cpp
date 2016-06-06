#include "Save_File.h"
#include <fstream>
#include <iostream>

namespace Games
{
	const std::string FIRE_VERSION = "#Fire";
	const std::string LIFE_VERSION = "#Life";
	const std::string NAME_MARK = "#N ";
	const std::string SIZE_MARK = "#S ";
	const std::string DELIM = " ";
	const std::string WALL = "W ";
	const std::string FIRE = "F ";
	const std::string HUMAN = "H ";

	void Save::save_file(const std::string filename, const Field<Cell>& universe, const Strategy& strategy) const
	{
		std::ofstream output_file(filename);
		if (!output_file)
		{
			throw Exception::cannot_open_output_file(filename);
		}
		if (strategy.get_rules().empty())
		{
			output_file << FIRE_VERSION << std::endl;
			output_file << NAME_MARK << universe.getName() << std::endl;
			size_t width = universe.getWidth();
			size_t height = universe.getHeight();
			output_file << SIZE_MARK << width << DELIM << height << std::endl;
			for (size_t i = 0; i < width; ++i)
			{
				for (size_t j = 0; j < height; ++j)
				{
					Type t = universe.getCell(i, j).get_type();
					switch (t)
					{
					case Games::CL_WALL:
						output_file << WALL << i << DELIM << j << std::endl;
						break;
					case Games::CL_FIRE:
						output_file << FIRE << i << DELIM << j << std::endl;
						break;
					case Games::CL_HUMAN:
						output_file << HUMAN << i << DELIM << j << std::endl;
						break;
					default:
						break;
					}
				}
			}
			return;
		}
		else
		{
			output_file << LIFE_VERSION << std::endl;
			output_file << NAME_MARK << universe.getName() << std::endl;
			output_file << strategy.get_rules() << std::endl;
			size_t width = universe.getWidth();
			size_t height = universe.getHeight();
			output_file << SIZE_MARK << width << DELIM << height << std::endl;
			for (size_t i = 0; i < width; ++i)
			{
				for (size_t j = 0; j < height; ++j)
				{
					Type type = universe.getCell(i, j).get_type();
					if (type == Games::CL_WALL)
					{
						output_file << i << DELIM << j << std::endl;
					}
				}
			}
			return;
		}
	}
}