#ifndef SAVE_FILE_H
#define SAVE_FILE_H
#include "Field.h"
#include "Strategy.h"

namespace Games
{
	class Save
	{
	public:
		Save();
		~Save();
		void save_file(const std::string filename, const Field<Cell>& universe, const Strategy& strategy) const;
	};

	namespace Exception
	{
		class save_file : public std::exception
		{
		public:
			explicit save_file(const std::string& why)
				: reason(why) {}
			const char* what() const throw()
			{
				return reason.c_str();
			}

		private:
			std::string reason;
		};
		class cannot_open_output_file : public save_file
		{
		public:
			explicit cannot_open_output_file(const std::string& why)
				: save_file("Can not open the '" + why + "' file") {}
		};
	}
}
#endif