import sys
from flask import Flask
from flask import render_template
from flask import request
import psycopg2

app = Flask(__name__)

@app.route('/')
def index():
    return render_template('fromMain.html')
    
@app.route('/classe')
def mostrar():
	
	ip = request.args.get('ip') 
	port = request.args.get('port') 
	bbdd = request.args.get('bbdd') 
	usuari = request.args.get('usuari') 
	contrasenya = request.args.get('contrasenya') 
	taula = request.args.get('taula') 
	print(taula)
	sql = request.args.get('sql') 
	
	conn = psycopg2.connect(host=ip, port=port, database=bbdd, user=usuari, password=contrasenya)

	cur = conn.cursor()
	query = "SELECT * FROM " + taula
	outputquery = "COPY ({0}) TO STDOUT WITH CSV HEADER".format(query)

	with open(taula+'.csv', 'w') as f:
		cur.copy_expert(outputquery, f)
    
	if(taula != ""):
		cur.execute("SELECT * FROM " + taula)
		resultat = cur.fetchall()
		cur.close()
		conn.close()

	elif(sql != ""):
		cur.execute(sql)
		resultat = cur.fetchall()
		cur.close()
		conn.close()

	
	return render_template('fromMain.html', outputs = resultat, taula = taula)
	


if __name__=='__main__':
    app.run(host="0.0.0.0", debug=True)
