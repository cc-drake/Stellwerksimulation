Hinweise zu Strecken.csv:
StreckenID's müssen eindeutig sein.
Betriebsstellen sind Endpunkte von Fahrwegen und dürfen nicht mehr als zwei ausgehende Strecken besitzen.
Die Lage ist matrixcodiert (d.h. links oben ist (0,0).
Die Anschlussrichtung gibt an, in welche Richtungen eine Strecke verläuft, und werden mit / getrennt. Beispiel: "O/W" für eine Ost-West-Strecke. 
Um auf einer Strecke eine kreuzungsfreie Brücke zu zeichnen, müssen zwei Strecken mit identischen Koordinaten angelegt werden. Für beide Strecken ist dann im Feld "Brückenebene (oben/unten) der Eintrag "oben" bzw. "unten" vorzunehmen.
Die Positionsangaben zu Zügen und Bahnhofsnamen ermöglichen die Verschiebung der Textbox in Pixeln.

Hinweise zu Fahrwege.csv:
Für jeden Fahrweg muss angegeben sein, welche Fahrziele hierüber erreicht werden können. Die Fahrziele müssen in der Überschriftenzeile verzeichnet werden. Hierzu gibt es 3 Möglichkeiten:
- Der Eintrag "b" bedeutet, dass der Weg zu diesem Fahrziel über den Bahnhof führt. Züge mit diesem Fahrziel können diesen Fahrweg daher problemlos nutzen.
- Der Eintrag "x" bedeutet, dass zwar ein Weg zum Bahnhof vorhanden ist, dieser jedoch nicht über einen Bahnhof führt. Daher werden Züge mit diesem Fahrziel diesen Fahrweg nur nutzen, wenn sie den Fahrgastwechsel bereits erledigt haben oder Güterzüge sind.
- Ein leerer Eintrag bedeutet, dass dieses Fahrziel nicht über diesen Fahrweg erreichbar ist. Entsprechend werden Züge mit diesem Fahrziel diesen Fahrweg nicht nutzen können.
Hinweis: Rangierende Kurswagen können unabhängig von ihrem Fahrtziel jeden Fahrweg nutzen.
In den weiteren Spalten (Ab Überschrift "Laufweg") wird der Verlauf des Fahrweges definiert. Die Angabe einer Fahrtrichtung ist für kreuzungsfreie Strecken optional, andernfalls hat die Richtungsangabe mit einem "$" an die entsprechende Strecke angehängt zu werden. Beispiel: Strecke 1a$O.
Die Richtungscodierung ist wie bei den Strecken, d.h. Nord=N, Nordost=NO,...
Es ist zu beachten, dass ein Fahrweg jede Strecke nur einmal berühren darf.
Das Einfügen freier Zeilen zwischen einzelnen Fahrwegsblöcken ist der Übersichtlichkeit halber erlaubt.

Hinweise zu Fahrplan.csv:
Im Feld Gleis ist der Name der Betriebsstelle anzugeben, an der der Fahrgastwechsel planmäßig stattfinden soll.
Für Güterzüge oder andere Züge ohne Halt ist das Feld "Gleis" leer zu lassen.