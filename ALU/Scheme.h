#ifndef SCHEME_H
#define SCHEME_H

#include "Factory.h"

#include <string>
#include <vector>
#include <unordered_map>

namespace ALU 
{
    class Scheme 
	{
    public:
        std::vector<std::pair<std::string, pItem>> inputs, outputs;
        std::unordered_map<std::string, pItem> elements;
        Scheme(std::string);        
    };
}

#endif 