import tempfile
from urllib.request import urlretrieve

from bs4 import BeautifulSoup


def read_intraco():
    url = 'https://kantor-intraco.pl/kursy'
    tmpf = tempfile.NamedTemporaryFile()
    urlretrieve(url, tmpf.name)

    with open(tmpf.name, 'r') as fp:
        soup = BeautifulSoup(fp, 'html.parser')

        for td in soup.table.findChildren('td'):
            #print(td)
            if td.attrs['class'][0] == 'country' and 'euro' in td.contents:
                currency = td.find_next_sibling('td')
            #if 'EUR' in td.contents:
                buy = currency.find_next_sibling('td')
                sell = buy.find_next_sibling('td')
                #print(buy.select('class'))
                if buy.attrs['class'][0] == 'buy':
                    buy_pr = float(buy.text)
                    print(buy_pr)
                if sell.attrs['class'][0] == 'sell':
                    sell_pr = float(sell.text)
                    print(sell_pr)
                #print(td.value)
    return [buy_pr, sell_pr]
        #print(soup.table.findChildren('td'))
    #return buy_pr
    

def read_xxi():
    url = 'https://kantor-wiek.pl/kursy-walut-centrum-handlowe-arkadia/'
    tmpf = tempfile.NamedTemporaryFile()
    urlretrieve(url, tmpf.name)

    with open(tmpf.name, 'r') as fp:
        soup = BeautifulSoup(fp, 'html.parser')
        for td in soup.table.findChildren('td'):
            print(td)
            cnts = [x.strip() for x in td.contents if x is not None]
            if 'Euro' in cnts:
                print(td)
            #     currency = td.find_next_sibling('td')
            #     # if 'EUR' in td.contents:
            #     buy = currency.find_next_sibling('td')
            #     sell = buy.find_next_sibling('td')
            #     # print(buy.select('class'))
            #     if buy.attrs['class'][0] == 'buy':
            #         buy_pr = float(buy.text)
            #         print(buy_pr)
            #     if sell.attrs['class'][0] == 'sell':
            #         sell_pr = float(sell.text)
            #         print(sell_pr)
            #     # print(td.value)
#    return buy_pr, sell_pr


#read_xxi()
