goods = []
goods_to_suppliers = {}
suppliers = []
teams = []

print("Podaj liczbe dostawcow")
m = int(input())
for _ in range(m):
    goods.append(str(input()))

print('Podaj liczbe ekip')
e = int(input())
for _ in range(e):
    teams.append(str(input()))

print("podaj licze produktow")
n = int(input())
for _ in range(n):
    goods.append(str(input()))
print("W kazdej kolejnej lini podaj ilosc i nastepniej liste dostawcow dla produktow")
for _ in range(n):
    k = int(input())
    for _ in range(k):
        goods_to_suppliers[goods[k]] = goods_to_suppliers.get(goods[k], []).append(str(input()))
