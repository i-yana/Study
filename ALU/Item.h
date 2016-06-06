#ifndef ITEM_H
#define ITEM_H
#include <vector>
#include <memory>

namespace ALU 
{
	using bitArray = std::vector < bool >;
	const std::string TAB = "\t";
	const bool INITSTATE = false;
	const char _TRUE = '1';
	const char _FALSE = '0';

	enum ItemType 
	{
		INPUT = 0,
		OUTPUT,
		MULTIPLEXER,
		LOGIC
	};

	class Item;
	using pItem = std::shared_ptr<Item>;

    class Item
	{
    public:
        Item();
		virtual ~Item();
        bool ready();
        void refreshValue();
        ItemType type;

        virtual bool getValue() = 0;
        virtual bool addInput(pItem) = 0;
    protected:
        void setReady();
        bool m_ready;
        bool m_processed;
        bool m_value;
    };
}

#endif
