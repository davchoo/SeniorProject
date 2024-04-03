<?xml version="1.0" encoding="UTF-8"?>
<StyledLayerDescriptor version="1.0.0"
    xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd"
    xmlns="http://www.opengis.net/sld"
    xmlns:ogc="http://www.opengis.net/ogc"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <NamedLayer>
    <Name>Temperature K to F</Name>
    <UserStyle>
      <Title>Temperature</Title>
      <FeatureTypeStyle>
        <Transformation>
          <ogc:Function name="ras:Jiffle">
            <ogc:Function name="parameter">
              <ogc:Literal>coverage</ogc:Literal>
            </ogc:Function>
            <ogc:Function name="parameter">
              <ogc:Literal>script</ogc:Literal>
              <ogc:Literal>
                dest = (src[0] - 273.15) * 9.0 / 5.0 + 32.0;
              </ogc:Literal>
            </ogc:Function>
          </ogc:Function>
        </Transformation>
        <Rule>
          <RasterSymbolizer>
            <ColorMap>
<ColorMapEntry color="#b8abb8" quantity="-100.0" opacity="0" />
<ColorMapEntry color="#b8abb8" quantity="-10.0" opacity="1"/>
<ColorMapEntry color="#bbadbb" quantity="-9.5" opacity="1" />
<ColorMapEntry color="#beafbe" quantity="-9.0" opacity="1" />
<ColorMapEntry color="#c1b0c1" quantity="-8.5" opacity="1" />
<ColorMapEntry color="#c5b2c5" quantity="-8.0" opacity="1" />
<ColorMapEntry color="#c8b3c8" quantity="-7.5" opacity="1" />
<ColorMapEntry color="#ccb5cc" quantity="-7.0" opacity="1" />
<ColorMapEntry color="#cfb6cf" quantity="-6.5" opacity="1" />
<ColorMapEntry color="#d3b8d3" quantity="-6.0" opacity="1" />
<ColorMapEntry color="#d6b9d6" quantity="-5.5" opacity="1" />
<ColorMapEntry color="#dabbda" quantity="-5.0" opacity="1" />
<ColorMapEntry color="#ddbcdd" quantity="-4.5" opacity="1" />
<ColorMapEntry color="#e1bee1" quantity="-4.0" opacity="1" />
<ColorMapEntry color="#e5bfe5" quantity="-3.5" opacity="1" />
<ColorMapEntry color="#e9c0e9" quantity="-3.0" opacity="1" />
<ColorMapEntry color="#ecc1ec" quantity="-2.5" opacity="1" />
<ColorMapEntry color="#f0c3f0" quantity="-2.0" opacity="1" />
<ColorMapEntry color="#f4c4f4" quantity="-1.5" opacity="1" />
<ColorMapEntry color="#f8c6f8" quantity="-1.0" opacity="1" />
<ColorMapEntry color="#fbc7fb" quantity="-0.5" opacity="1" />
<ColorMapEntry color="#ffc8ff" quantity="0.0" opacity="1" label="0" />
<ColorMapEntry color="#ffbeff" quantity="0.5" opacity="1" />
<ColorMapEntry color="#ffb4ff" quantity="1.0" opacity="1" />
<ColorMapEntry color="#fdb2fd" quantity="1.5" opacity="1" />
<ColorMapEntry color="#fba5fb" quantity="2.0" opacity="1" />
<ColorMapEntry color="#faa0f9" quantity="2.5" opacity="1" />
<ColorMapEntry color="#f896f8" quantity="3.0" opacity="1" />
<ColorMapEntry color="#f594f5" quantity="3.5" opacity="1" />
<ColorMapEntry color="#f391f3" quantity="4.0" opacity="1" />
<ColorMapEntry color="#f08ff0" quantity="4.5" opacity="1" />
<ColorMapEntry color="#ef8cef" quantity="5.0" opacity="1" />
<ColorMapEntry color="#ed8aed" quantity="5.5" opacity="1" />
<ColorMapEntry color="#eb87eb" quantity="6.0" opacity="1" />
<ColorMapEntry color="#e984e9" quantity="6.5" opacity="1" />
<ColorMapEntry color="#e782e7" quantity="7.0" opacity="1" />
<ColorMapEntry color="#e580e5" quantity="7.5" opacity="1" />
<ColorMapEntry color="#e37de3" quantity="8.0" opacity="1" />
<ColorMapEntry color="#e07be2" quantity="8.5" opacity="1" />
<ColorMapEntry color="#dd78e1" quantity="9.0" opacity="1" />
<ColorMapEntry color="#db76df" quantity="9.5" opacity="1" />
<ColorMapEntry color="#d873dc" quantity="10.0" opacity="1" label="10" />
<ColorMapEntry color="#d671d9" quantity="10.5" opacity="1" />
<ColorMapEntry color="#d36ed7" quantity="11.0" opacity="1" />
<ColorMapEntry color="#d16bd5" quantity="11.5" opacity="1" />
<ColorMapEntry color="#cf69d4" quantity="12.0" opacity="1" />
<ColorMapEntry color="#cd67d3" quantity="12.5" opacity="1" />
<ColorMapEntry color="#ca64d2" quantity="13.0" opacity="1" />
<ColorMapEntry color="#c862cd" quantity="13.5" opacity="1" />
<ColorMapEntry color="#c55fc8" quantity="14.0" opacity="1" />
<ColorMapEntry color="#c25dc7" quantity="14.5" opacity="1" />
<ColorMapEntry color="#bf5ac6" quantity="15.0" opacity="1" />
<ColorMapEntry color="#ba58c5" quantity="15.5" opacity="1" />
<ColorMapEntry color="#b656c4" quantity="16.0" opacity="1" />
<ColorMapEntry color="#b252c3" quantity="16.5" opacity="1" />
<ColorMapEntry color="#ad4fc1" quantity="17.0" opacity="1" />
<ColorMapEntry color="#a94bc0" quantity="17.5" opacity="1" />
<ColorMapEntry color="#a448bf" quantity="18.0" opacity="1" />
<ColorMapEntry color="#a044be" quantity="18.5" opacity="1" />
<ColorMapEntry color="#9b41bd" quantity="19.0" opacity="1" />
<ColorMapEntry color="#963dbc" quantity="19.5" opacity="1" />
<ColorMapEntry color="#923abb" quantity="20.0" opacity="1" label="20" />
<ColorMapEntry color="#8c36ba" quantity="20.5" opacity="1" />
<ColorMapEntry color="#8732b9" quantity="21.0" opacity="1" />
<ColorMapEntry color="#8230b8" quantity="21.5" opacity="1" />
<ColorMapEntry color="#7e2db6" quantity="22.0" opacity="1" />
<ColorMapEntry color="#7a2bb4" quantity="22.5" opacity="1" />
<ColorMapEntry color="#7528b2" quantity="23.0" opacity="1" />
<ColorMapEntry color="#7026b0" quantity="23.5" opacity="1" />
<ColorMapEntry color="#6c23ae" quantity="24.0" opacity="1" />
<ColorMapEntry color="#6821ac" quantity="24.5" opacity="1" />
<ColorMapEntry color="#631eaa" quantity="25.0" opacity="1" />
<ColorMapEntry color="#5f1ca8" quantity="25.5" opacity="1" />
<ColorMapEntry color="#5a19a6" quantity="26.0" opacity="1" />
<ColorMapEntry color="#5517a4" quantity="26.5" opacity="1" />
<ColorMapEntry color="#5014a2" quantity="27.0" opacity="1" />
<ColorMapEntry color="#4b12a0" quantity="27.5" opacity="1" />
<ColorMapEntry color="#460f9e" quantity="28.0" opacity="1" />
<ColorMapEntry color="#41149c" quantity="28.5" opacity="1" />
<ColorMapEntry color="#3c199a" quantity="29.0" opacity="1" />
<ColorMapEntry color="#3a1e99" quantity="29.5" opacity="1" />
<ColorMapEntry color="#372398" quantity="30.0" opacity="1" label="30" />
<ColorMapEntry color="#322897" quantity="30.5" opacity="1" />
<ColorMapEntry color="#2d2d96" quantity="31.0" opacity="1" />
<ColorMapEntry color="#2b329b" quantity="31.5" opacity="1" />
<ColorMapEntry color="#2837a0" quantity="32.0" opacity="1" />
<ColorMapEntry color="#263ca5" quantity="32.5" opacity="1" />
<ColorMapEntry color="#2341aa" quantity="33.0" opacity="1" />
<ColorMapEntry color="#2149af" quantity="33.5" opacity="1" />
<ColorMapEntry color="#1e50b4" quantity="34.0" opacity="1" />
<ColorMapEntry color="#1c55b9" quantity="34.5" opacity="1" />
<ColorMapEntry color="#195abe" quantity="35.0" opacity="1" />
<ColorMapEntry color="#175fc1" quantity="35.5" opacity="1" />
<ColorMapEntry color="#1464c3" quantity="36.0" opacity="1" />
<ColorMapEntry color="#126ec6" quantity="36.5" opacity="1" />
<ColorMapEntry color="#0f78c8" quantity="37.0" opacity="1" />
<ColorMapEntry color="#0d82cd" quantity="37.5" opacity="1" />
<ColorMapEntry color="#0c8cd2" quantity="38.0" opacity="1" />
<ColorMapEntry color="#0b96d5" quantity="38.5" opacity="1" />
<ColorMapEntry color="#0aa0d7" quantity="39.0" opacity="1" />
<ColorMapEntry color="#09abda" quantity="39.5" opacity="1" />
<ColorMapEntry color="#07b6dc" quantity="40.0" opacity="1" label="40" />
<ColorMapEntry color="#06b9e1" quantity="40.5" opacity="1" />
<ColorMapEntry color="#05bde6" quantity="41.0" opacity="1" />
<ColorMapEntry color="#03bfeb" quantity="41.5" opacity="1" />
<ColorMapEntry color="#02c1f0" quantity="42.0" opacity="1" />
<ColorMapEntry color="#01c3f2" quantity="42.5" opacity="1" />
<ColorMapEntry color="#00c6f5" quantity="43.0" opacity="1" />
<ColorMapEntry color="#00c9f3" quantity="43.5" opacity="1" />
<ColorMapEntry color="#00cdf0" quantity="44.0" opacity="1" />
<ColorMapEntry color="#00cfea" quantity="44.5" opacity="1" />
<ColorMapEntry color="#00d2e4" quantity="45.0" opacity="1" />
<ColorMapEntry color="#00d5dc" quantity="45.5" opacity="1" />
<ColorMapEntry color="#01d7d4" quantity="46.0" opacity="1" />
<ColorMapEntry color="#01dacd" quantity="46.5" opacity="1" />
<ColorMapEntry color="#01dcc6" quantity="47.0" opacity="1" />
<ColorMapEntry color="#01dcba" quantity="47.5" opacity="1" />
<ColorMapEntry color="#01dcb4" quantity="48.0" opacity="1" />
<ColorMapEntry color="#01dcae" quantity="48.5" opacity="1" />
<ColorMapEntry color="#01dca7" quantity="49.0" opacity="1" />
<ColorMapEntry color="#01da91" quantity="49.5" opacity="1" />
<ColorMapEntry color="#01d786" quantity="50.0" opacity="1" label="50" />
<ColorMapEntry color="#01d577" quantity="50.5" opacity="1" />
<ColorMapEntry color="#01d264" quantity="51.0" opacity="1" />
<ColorMapEntry color="#00cf55" quantity="51.5" opacity="1" />
<ColorMapEntry color="#00cd46" quantity="52.0" opacity="1" />
<ColorMapEntry color="#00cb3c" quantity="52.5" opacity="1" />
<ColorMapEntry color="#00c832" quantity="53.0" opacity="1" />
<ColorMapEntry color="#00c528" quantity="53.5" opacity="1" />
<ColorMapEntry color="#05c31e" quantity="54.0" opacity="1" />
<ColorMapEntry color="#07c117" quantity="54.5" opacity="1" />
<ColorMapEntry color="#0ac00f" quantity="55.0" opacity="1" />
<ColorMapEntry color="#0fc00d" quantity="55.5" opacity="1" />
<ColorMapEntry color="#14c00a" quantity="56.0" opacity="1" />
<ColorMapEntry color="#19c106" quantity="56.5" opacity="1" />
<ColorMapEntry color="#1ec301" quantity="57.0" opacity="1" />
<ColorMapEntry color="#27c301" quantity="57.5" opacity="1" />
<ColorMapEntry color="#2dc401" quantity="58.0" opacity="1" />
<ColorMapEntry color="#37c501" quantity="58.5" opacity="1" />
<ColorMapEntry color="#41c702" quantity="59.0" opacity="1" />
<ColorMapEntry color="#4eca02" quantity="59.5" opacity="1" />
<ColorMapEntry color="#5fcd03" quantity="60.0" opacity="1" label="60" />
<ColorMapEntry color="#6ccf03" quantity="60.5" opacity="1" />
<ColorMapEntry color="#78d003" quantity="61.0" opacity="1" />
<ColorMapEntry color="#85d103" quantity="61.5" opacity="1" />
<ColorMapEntry color="#91d203" quantity="62.0" opacity="1" />
<ColorMapEntry color="#a0d503" quantity="62.5" opacity="1" />
<ColorMapEntry color="#aad704" quantity="63.0" opacity="1" />
<ColorMapEntry color="#b9da04" quantity="63.5" opacity="1" />
<ColorMapEntry color="#c8dc05" quantity="64.0" opacity="1" />
<ColorMapEntry color="#d2df05" quantity="64.5" opacity="1" />
<ColorMapEntry color="#dce105" quantity="65.0" opacity="1" />
<ColorMapEntry color="#e4e304" quantity="65.5" opacity="1" />
<ColorMapEntry color="#ebe604" quantity="66.0" opacity="1" />
<ColorMapEntry color="#eee903" quantity="66.5" opacity="1" />
<ColorMapEntry color="#f0eb03" quantity="67.0" opacity="1" />
<ColorMapEntry color="#f3ee03" quantity="67.5" opacity="1" />
<ColorMapEntry color="#f5f003" quantity="68.0" opacity="1" />
<ColorMapEntry color="#f8f502" quantity="68.5" opacity="1" />
<ColorMapEntry color="#faf801" quantity="69.0" opacity="1" />
<ColorMapEntry color="#fdfb01" quantity="69.5" opacity="1" />
<ColorMapEntry color="#ffff00" quantity="70.0" opacity="1" label="70" />
<ColorMapEntry color="#fffa00" quantity="70.5" opacity="1" />
<ColorMapEntry color="#fff500" quantity="71.0" opacity="1" />
<ColorMapEntry color="#fff000" quantity="71.5" opacity="1" />
<ColorMapEntry color="#ffeb00" quantity="72.0" opacity="1" />
<ColorMapEntry color="#ffe600" quantity="72.5" opacity="1" />
<ColorMapEntry color="#ffe100" quantity="73.0" opacity="1" />
<ColorMapEntry color="#ffdc00" quantity="73.5" opacity="1" />
<ColorMapEntry color="#ffd700" quantity="74.0" opacity="1" />
<ColorMapEntry color="#ffd200" quantity="74.5" opacity="1" />
<ColorMapEntry color="#ffc800" quantity="75.0" opacity="1" />
<ColorMapEntry color="#ffc300" quantity="75.5" opacity="1" />
<ColorMapEntry color="#ffbe00" quantity="76.0" opacity="1" />
<ColorMapEntry color="#ffb900" quantity="76.5" opacity="1" />
<ColorMapEntry color="#feb400" quantity="77.0" opacity="1" />
<ColorMapEntry color="#feaa00" quantity="77.5" opacity="1" />
<ColorMapEntry color="#fda000" quantity="78.0" opacity="1" />
<ColorMapEntry color="#fd9600" quantity="78.5" opacity="1" />
<ColorMapEntry color="#fc8c00" quantity="79.0" opacity="1" />
<ColorMapEntry color="#fc8200" quantity="79.5" opacity="1" />
<ColorMapEntry color="#fb7800" quantity="80.0" opacity="1" label="80" />
<ColorMapEntry color="#fa6e00" quantity="80.5" opacity="1" />
<ColorMapEntry color="#f96400" quantity="81.0" opacity="1" />
<ColorMapEntry color="#f85a00" quantity="81.5" opacity="1" />
<ColorMapEntry color="#f65000" quantity="82.0" opacity="1" />
<ColorMapEntry color="#f44b00" quantity="82.5" opacity="1" />
<ColorMapEntry color="#f34600" quantity="83.0" opacity="1" />
<ColorMapEntry color="#f24100" quantity="83.5" opacity="1" />
<ColorMapEntry color="#f03c00" quantity="84.0" opacity="1" />
<ColorMapEntry color="#ee3a00" quantity="84.5" opacity="1" />
<ColorMapEntry color="#eb3800" quantity="85.0" opacity="1" />
<ColorMapEntry color="#e93601" quantity="85.5" opacity="1" />
<ColorMapEntry color="#e63402" quantity="86.0" opacity="1" />
<ColorMapEntry color="#e43202" quantity="86.5" opacity="1" />
<ColorMapEntry color="#e13002" quantity="87.0" opacity="1" />
<ColorMapEntry color="#df2e02" quantity="87.5" opacity="1" />
<ColorMapEntry color="#dc2c02" quantity="88.0" opacity="1" />
<ColorMapEntry color="#da2a02" quantity="88.5" opacity="1" />
<ColorMapEntry color="#d72802" quantity="89.0" opacity="1" />
<ColorMapEntry color="#d52602" quantity="89.5" opacity="1" />
<ColorMapEntry color="#d22402" quantity="90.0" opacity="1" label="90" />
<ColorMapEntry color="#cf2202" quantity="90.5" opacity="1" />
<ColorMapEntry color="#cd2002" quantity="91.0" opacity="1" />
<ColorMapEntry color="#ca1e02" quantity="91.5" opacity="1" />
<ColorMapEntry color="#c81c02" quantity="92.0" opacity="1" />
<ColorMapEntry color="#c61a02" quantity="92.5" opacity="1" />
<ColorMapEntry color="#c31802" quantity="93.0" opacity="1" />
<ColorMapEntry color="#c11602" quantity="93.5" opacity="1" />
<ColorMapEntry color="#be1402" quantity="94.0" opacity="1" />
<ColorMapEntry color="#bb1302" quantity="94.5" opacity="1" />
<ColorMapEntry color="#b91202" quantity="95.0" opacity="1" />
<ColorMapEntry color="#b71102" quantity="95.5" opacity="1" />
<ColorMapEntry color="#b41002" quantity="96.0" opacity="1" />
<ColorMapEntry color="#b20f02" quantity="96.5" opacity="1" />
<ColorMapEntry color="#af0e02" quantity="97.0" opacity="1" />
<ColorMapEntry color="#ac0d02" quantity="97.5" opacity="1" />
<ColorMapEntry color="#aa0c02" quantity="98.0" opacity="1" />
<ColorMapEntry color="#a80b02" quantity="98.5" opacity="1" />
<ColorMapEntry color="#a50a02" quantity="99.0" opacity="1" />
<ColorMapEntry color="#a30902" quantity="99.5" opacity="1" />
<ColorMapEntry color="#a00802" quantity="100.0" opacity="1" label="100" />
<ColorMapEntry color="#a50e0c" quantity="100.5" opacity="1" />
<ColorMapEntry color="#aa1516" quantity="101.0" opacity="1" />
<ColorMapEntry color="#ae1f22" quantity="101.5" opacity="1" />
<ColorMapEntry color="#b32a2f" quantity="102.0" opacity="1" />
<ColorMapEntry color="#b32a2f" quantity="102.5" opacity="1" />
<ColorMapEntry color="#bc4048" quantity="103.0" opacity="1" />
<ColorMapEntry color="#bc4048" quantity="103.5" opacity="1" />
<ColorMapEntry color="#c55560" quantity="104.0" opacity="1" />
<ColorMapEntry color="#c95f6b" quantity="104.5" opacity="1" />
<ColorMapEntry color="#cd6a77" quantity="105.0" opacity="1" />
<ColorMapEntry color="#d17482" quantity="105.5" opacity="1" />
<ColorMapEntry color="#d67f8d" quantity="106.0" opacity="1" />
<ColorMapEntry color="#d98998" quantity="106.5" opacity="1" />
<ColorMapEntry color="#dd94a3" quantity="107.0" opacity="1" />
<ColorMapEntry color="#e19ead" quantity="107.5" opacity="1" />
<ColorMapEntry color="#e5a9b7" quantity="108.0" opacity="1" />
<ColorMapEntry color="#e8b4c1" quantity="108.5" opacity="1" />
<ColorMapEntry color="#ecbfcb" quantity="109.0" opacity="1" />
<ColorMapEntry color="#efc9d4" quantity="109.5" opacity="1" />
<ColorMapEntry color="#f2d4dd" quantity="110.0" opacity="1"/>
            </ColorMap>
          </RasterSymbolizer>
        </Rule>
      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>