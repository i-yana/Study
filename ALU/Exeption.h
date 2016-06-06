#ifndef EXCEPTION_H
#define EXCEPTION_H

#include <string>

namespace ALU 
{
	namespace AluException
	{
		class ALUExeption : public std::exception
		{
		public:
			explicit ALUExeption(const std::string& why)
				: reason(why){}
			const char* what() const throw()
			{
				return reason.c_str();
			}
		private:
			std::string reason;
		};

		class Not_Correct_Scheme : public ALUExeption 
		{
		public:
			explicit Not_Correct_Scheme() : ALUExeption("Schema has cicrcle"){};
		};

		class Not_input_Item_state : public ALUExeption {
		public:
			explicit Not_input_Item_state() : ALUExeption("Not right state of the current Item"){};
		};

		class Factory_exeption : public ALUExeption {
		public:
			explicit Factory_exeption(const std::string& why) 
				: ALUExeption("Not resolve item type cann't create Item" + why){};
		};

		class Error_of_opening_input_file : public ALUExeption {
		public:
			explicit Error_of_opening_input_file(const std::string& why)
				: ALUExeption("Cann't open input file " + why){};
		};

		class Parser_exception : public ALUExeption {
		public:
			explicit Parser_exception() : ALUExeption("Not correct scheme file"){};
		};
	}
}
#endif
