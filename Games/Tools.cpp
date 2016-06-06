#include "Tools.h"


namespace Games
{
	namespace Tools
	{
		bool get_numbers(std::vector<int> & result, const std::string & s)
		{
			bool found = false;
			bool res = false;
			int number = 0;

			for (std::string::size_type i = 0; i < s.length(); i++) {
				const char ch = s[i];
				if (ch >= '0' && ch <= '9') {
					const int digit = ch - '0';
					number = number * 10 + digit;
					found = true;
				}
				else {
					if (found) {
						result.push_back(number);
						res = true;
						number = 0;
						found = false;
					}
				}
			}

			if (found) {
				res = true;
				result.push_back(number);
			}
			return res;
		}
	}
}
