#ifndef LOGICITEM_H
#define LOGICITEM_H

#include "Item.h"
#include <functional>

namespace ALU {
    class LogicItem: public Item 
	{
    public:
        LogicItem(std::function<bool(bool,bool)>);
        ~LogicItem(){};
        bool getValue();
        bool addInput(pItem);
    protected:
        pItem a_in;
        pItem b_in;
        std::function<bool(bool,bool)> _op;
    };
}


#endif
