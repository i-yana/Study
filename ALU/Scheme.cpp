#include "Scheme.h"
#include "Factory.h"
#include "Exeption.h"

#include "json11.hpp"
#include <fstream>
#include <sstream>
#include <algorithm>
#include <iostream>

using namespace json11;

using namespace ALU;

Scheme::Scheme(const std::string filename)
{
	using Elements = std::vector < std::pair<std::string, pItem> > ;
	std::fstream file(filename);
	if (!file.is_open())
	{
		throw AluException::Error_of_opening_input_file(filename);
	}
	file.open(filename);
	std::stringstream buffer;
	buffer << file.rdbuf();
	std::string error;
	json11::Json alu = json11::Json::parse(buffer.str(), error);
	if (!error.empty())
	{
		throw AluException::Parser_exception();
	}
	ItemFactory factory;
	Elements inOperations;
	for (auto obj : alu[ALU_MARK].array_items()) 
	{
		std::string id = obj[ID_MARK].string_value();
		std::string type = obj[TYPE].string_value();

		pItem item = factory.indicateItem(type);

		if (item->type == INPUT)
		{
			if (id.find(OP_MARK) != std::string::npos)
			{
				inOperations.push_back(std::make_pair(id, item));
			}
			else
			{
				inputs.push_back(std::make_pair(id, item));
			}
		}
		
		if (item->type == OUTPUT)
		{
			outputs.push_back(std::make_pair(id, item));
		}
		elements.insert(std::make_pair(id, item));
	}

	for (auto t : inOperations)
		inputs.push_back(t);

	for (auto obj : alu[ALU_MARK].array_items()) 
	{
		if (!obj[INPUTS].is_null())
		{
			auto id = obj[ID_MARK].string_value();
			auto item = (*elements.find(id)).second;

			for (auto input : obj[INPUTS].array_items()) 
			{
				auto inputName = input.string_value();

				if (inputName.size() > 0) 
				{
					auto inElem = elements.find(inputName);
					auto inItem = (*inElem).second;
					if ((item->type == MULTIPLEXER) && ((*inElem).first.find(OP_MARK) != std::string::npos))
					{
						(dynamic_cast<MultiPlexer*>(&(*(item)))->addControlInput(inItem));
					}
					else
					{
						item->addInput(inItem);
					}
				}
			}
		}
	}
}
