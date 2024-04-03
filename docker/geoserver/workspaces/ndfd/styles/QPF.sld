<?xml version="1.0" encoding="UTF-8"?>
<StyledLayerDescriptor version="1.0.0"
    xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd"
    xmlns="http://www.opengis.net/sld"
    xmlns:ogc="http://www.opengis.net/ogc"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <NamedLayer>
    <Name>QPF</Name>
    <UserStyle>
      <Title>QPF</Title>
      <FeatureTypeStyle>
        <Transformation>
          <ogc:Function name="ras:Jiffle">
            <ogc:Function name="parameter">
              <ogc:Literal>coverage</ogc:Literal>
            </ogc:Function>
            <ogc:Function name="parameter">
              <ogc:Literal>script</ogc:Literal>
              <ogc:Literal>
                dest = src[0] / 10.0 / 2.54;
              </ogc:Literal>
            </ogc:Function>
          </ogc:Function>
        </Transformation>
        <Rule>
          <RasterSymbolizer>
            <ColorMap>
<ColorMapEntry color="#ffffff" quantity="0.0" opacity="0" label="0" />
<ColorMapEntry color="#d3ecce" quantity="0.05" opacity="1" />
<ColorMapEntry color="#c6eeb8" quantity="0.1" opacity="1" />
<ColorMapEntry color="#b8f0a1" quantity="0.15" opacity="1" />
<ColorMapEntry color="#aaf28a" quantity="0.2" opacity="1" />
<ColorMapEntry color="#9cf474" quantity="0.25" opacity="1" />
<ColorMapEntry color="#8df65d" quantity="0.3" opacity="1" />
<ColorMapEntry color="#80f847" quantity="0.35" opacity="1" />
<ColorMapEntry color="#73fb31" quantity="0.4" opacity="1" />
<ColorMapEntry color="#64fd1a" quantity="0.45" opacity="1" />
<ColorMapEntry color="#56ff04" quantity="0.5" opacity="1" />
<ColorMapEntry color="#50f200" quantity="0.55" opacity="1" />
<ColorMapEntry color="#4be300" quantity="0.6" opacity="1" />
<ColorMapEntry color="#46d400" quantity="0.65" opacity="1" />
<ColorMapEntry color="#41c500" quantity="0.7" opacity="1" />
<ColorMapEntry color="#3bb500" quantity="0.75" opacity="1" />
<ColorMapEntry color="#37a700" quantity="0.8" opacity="1" />
<ColorMapEntry color="#309400" quantity="0.85" opacity="1" />
<ColorMapEntry color="#2b8500" quantity="0.9" opacity="1" />
<ColorMapEntry color="#267600" quantity="0.95" opacity="1" />
<ColorMapEntry color="#216600" quantity="1.0" opacity="1" label="1" />
<ColorMapEntry color="#2c6d00" quantity="1.05" opacity="1" />
<ColorMapEntry color="#367500" quantity="1.1" opacity="1" />
<ColorMapEntry color="#417c00" quantity="1.15" opacity="1" />
<ColorMapEntry color="#4d8500" quantity="1.2" opacity="1" />
<ColorMapEntry color="#588c00" quantity="1.25" opacity="1" />
<ColorMapEntry color="#639400" quantity="1.3" opacity="1" />
<ColorMapEntry color="#6e9b00" quantity="1.35" opacity="1" />
<ColorMapEntry color="#79a300" quantity="1.4" opacity="1" />
<ColorMapEntry color="#85ab00" quantity="1.45" opacity="1" />
<ColorMapEntry color="#8fb200" quantity="1.5" opacity="1" />
<ColorMapEntry color="#9ab900" quantity="1.55" opacity="1" />
<ColorMapEntry color="#a6c100" quantity="1.6" opacity="1" />
<ColorMapEntry color="#b0c900" quantity="1.65" opacity="1" />
<ColorMapEntry color="#bbd000" quantity="1.7" opacity="1" />
<ColorMapEntry color="#c6d800" quantity="1.75" opacity="1" />
<ColorMapEntry color="#d1e000" quantity="1.8" opacity="1" />
<ColorMapEntry color="#dce700" quantity="1.85" opacity="1" />
<ColorMapEntry color="#e7ee00" quantity="1.9" opacity="1" />
<ColorMapEntry color="#f2f600" quantity="1.95" opacity="1" />
<ColorMapEntry color="#fefe00" quantity="2.0" opacity="1" label="2" />
<ColorMapEntry color="#fff702" quantity="2.05" opacity="1" />
<ColorMapEntry color="#ffec05" quantity="2.1" opacity="1" />
<ColorMapEntry color="#ffe207" quantity="2.15" opacity="1" />
<ColorMapEntry color="#ffd80a" quantity="2.2" opacity="1" />
<ColorMapEntry color="#ffce0d" quantity="2.25" opacity="1" />
<ColorMapEntry color="#ffc40f" quantity="2.3" opacity="1" />
<ColorMapEntry color="#ffb911" quantity="2.35" opacity="1" />
<ColorMapEntry color="#ffb014" quantity="2.4" opacity="1" />
<ColorMapEntry color="#ffa516" quantity="2.45" opacity="1" />
<ColorMapEntry color="#ff9b19" quantity="2.5" opacity="1" />
<ColorMapEntry color="#ff8f1c" quantity="2.55" opacity="1" />
<ColorMapEntry color="#ff851f" quantity="2.6" opacity="1" />
<ColorMapEntry color="#ff7b21" quantity="2.65" opacity="1" />
<ColorMapEntry color="#ff7023" quantity="2.7" opacity="1" />
<ColorMapEntry color="#ff6626" quantity="2.75" opacity="1" />
<ColorMapEntry color="#ff5d29" quantity="2.8" opacity="1" />
<ColorMapEntry color="#ff522c" quantity="2.85" opacity="1" />
<ColorMapEntry color="#ff482e" quantity="2.9" opacity="1" />
<ColorMapEntry color="#ff3e30" quantity="2.95" opacity="1" />
<ColorMapEntry color="#ff3433" quantity="3.0" opacity="1" label="3" />
<ColorMapEntry color="#fb3131" quantity="3.05" opacity="1" />
<ColorMapEntry color="#f52e2e" quantity="3.1" opacity="1" />
<ColorMapEntry color="#f02b2b" quantity="3.15" opacity="1" />
<ColorMapEntry color="#eb2929" quantity="3.2" opacity="1" />
<ColorMapEntry color="#e62626" quantity="3.25" opacity="1" />
<ColorMapEntry color="#e12424" quantity="3.3" opacity="1" />
<ColorMapEntry color="#dc2121" quantity="3.35" opacity="1" />
<ColorMapEntry color="#d71f1f" quantity="3.4" opacity="1" />
<ColorMapEntry color="#d21c1c" quantity="3.45" opacity="1" />
<ColorMapEntry color="#cc1919" quantity="3.5" opacity="1" />
<ColorMapEntry color="#c71717" quantity="3.55" opacity="1" />
<ColorMapEntry color="#c31515" quantity="3.6" opacity="1" />
<ColorMapEntry color="#be1212" quantity="3.65" opacity="1" />
<ColorMapEntry color="#b91010" quantity="3.7" opacity="1" />
<ColorMapEntry color="#b30d0d" quantity="3.75" opacity="1" />
<ColorMapEntry color="#ae0b0b" quantity="3.8" opacity="1" />
<ColorMapEntry color="#aa0909" quantity="3.85" opacity="1" />
<ColorMapEntry color="#a40606" quantity="3.9" opacity="1" />
<ColorMapEntry color="#9f0303" quantity="3.95" opacity="1" />
<ColorMapEntry color="#9a0101" quantity="4.0" opacity="1" label="4" />
<ColorMapEntry color="#9d0809" quantity="4.05" opacity="1" />
<ColorMapEntry color="#a21115" quantity="4.1" opacity="1" />
<ColorMapEntry color="#a61a21" quantity="4.15" opacity="1" />
<ColorMapEntry color="#ac252e" quantity="4.2" opacity="1" />
<ColorMapEntry color="#b02e3a" quantity="4.25" opacity="1" />
<ColorMapEntry color="#b53745" quantity="4.3" opacity="1" />
<ColorMapEntry color="#b94051" quantity="4.35" opacity="1" />
<ColorMapEntry color="#bf4b5d" quantity="4.4" opacity="1" />
<ColorMapEntry color="#c35468" quantity="4.45" opacity="1" />
<ColorMapEntry color="#c85d74" quantity="4.5" opacity="1" />
<ColorMapEntry color="#cd667f" quantity="4.55" opacity="1" />
<ColorMapEntry color="#d16f8b" quantity="4.6" opacity="1" />
<ColorMapEntry color="#d67997" quantity="4.65" opacity="1" />
<ColorMapEntry color="#db82a2" quantity="4.7" opacity="1" />
<ColorMapEntry color="#df8bad" quantity="4.75" opacity="1" />
<ColorMapEntry color="#e494b9" quantity="4.8" opacity="1" />
<ColorMapEntry color="#e89dc4" quantity="4.85" opacity="1" />
<ColorMapEntry color="#eda7d0" quantity="4.9" opacity="1" />
<ColorMapEntry color="#f2b0db" quantity="4.95" opacity="1" />
<ColorMapEntry color="#f7b9e7" quantity="5.0" opacity="1" label="5" />
<ColorMapEntry color="#f4b6e5" quantity="5.05" opacity="1" />
<ColorMapEntry color="#efb1e1" quantity="5.1" opacity="1" />
<ColorMapEntry color="#ecadde" quantity="5.15" opacity="1" />
<ColorMapEntry color="#e9a9db" quantity="5.2" opacity="1" />
<ColorMapEntry color="#e5a4d8" quantity="5.25" opacity="1" />
<ColorMapEntry color="#e1a0d4" quantity="5.3" opacity="1" />
<ColorMapEntry color="#dd9bd1" quantity="5.35" opacity="1" />
<ColorMapEntry color="#d997cd" quantity="5.4" opacity="1" />
<ColorMapEntry color="#d692ca" quantity="5.45" opacity="1" />
<ColorMapEntry color="#d28ec7" quantity="5.5" opacity="1" />
<ColorMapEntry color="#ce89c3" quantity="5.55" opacity="1" />
<ColorMapEntry color="#cb85c0" quantity="5.6" opacity="1" />
<ColorMapEntry color="#c780bc" quantity="5.65" opacity="1" />
<ColorMapEntry color="#c37cb9" quantity="5.7" opacity="1" />
<ColorMapEntry color="#bf77b6" quantity="5.75" opacity="1" />
<ColorMapEntry color="#bb73b2" quantity="5.8" opacity="1" />
<ColorMapEntry color="#b76dae" quantity="5.85" opacity="1" />
<ColorMapEntry color="#b368aa" quantity="5.9" opacity="1" />
<ColorMapEntry color="#af64a7" quantity="5.95" opacity="1" />
<ColorMapEntry color="#ac60a5" quantity="6.0" opacity="1" label="6" />
<ColorMapEntry color="#a85ba1" quantity="6.05" opacity="1" />
<ColorMapEntry color="#a4579e" quantity="6.1" opacity="1" />
<ColorMapEntry color="#a0529a" quantity="6.15" opacity="1" />
<ColorMapEntry color="#9d4e97" quantity="6.2" opacity="1" />
<ColorMapEntry color="#994a94" quantity="6.25" opacity="1" />
<ColorMapEntry color="#954590" quantity="6.3" opacity="1" />
<ColorMapEntry color="#91408c" quantity="6.35" opacity="1" />
<ColorMapEntry color="#8e3c89" quantity="6.4" opacity="1" />
<ColorMapEntry color="#8a3786" quantity="6.45" opacity="1" />
<ColorMapEntry color="#863382" quantity="6.5" opacity="1" />
<ColorMapEntry color="#822e7f" quantity="6.55" opacity="1" />
<ColorMapEntry color="#7f2a7c" quantity="6.6" opacity="1" />
<ColorMapEntry color="#7b2578" quantity="6.65" opacity="1" />
<ColorMapEntry color="#772074" quantity="6.7" opacity="1" />
<ColorMapEntry color="#731c71" quantity="6.75" opacity="1" />
<ColorMapEntry color="#70186e" quantity="6.8" opacity="1" />
<ColorMapEntry color="#6c136b" quantity="6.85" opacity="1" />
<ColorMapEntry color="#680f67" quantity="6.9" opacity="1" />
<ColorMapEntry color="#640a64" quantity="6.95" opacity="1" />
<ColorMapEntry color="#610661" quantity="7.0" opacity="1" label="7" />
<ColorMapEntry color="#620863" quantity="7.05" opacity="1" />
<ColorMapEntry color="#630b65" quantity="7.1" opacity="1" />
<ColorMapEntry color="#640d67" quantity="7.15" opacity="1" />
<ColorMapEntry color="#66106b" quantity="7.2" opacity="1" />
<ColorMapEntry color="#67136d" quantity="7.25" opacity="1" />
<ColorMapEntry color="#681670" quantity="7.3" opacity="1" />
<ColorMapEntry color="#691972" quantity="7.35" opacity="1" />
<ColorMapEntry color="#6b1c75" quantity="7.4" opacity="1" />
<ColorMapEntry color="#6c1e77" quantity="7.45" opacity="1" />
<ColorMapEntry color="#6d217a" quantity="7.5" opacity="1" />
<ColorMapEntry color="#6f247d" quantity="7.55" opacity="1" />
<ColorMapEntry color="#712880" quantity="7.6" opacity="1" />
<ColorMapEntry color="#722b83" quantity="7.65" opacity="1" />
<ColorMapEntry color="#732e85" quantity="7.7" opacity="1" />
<ColorMapEntry color="#753088" quantity="7.75" opacity="1" />
<ColorMapEntry color="#76338a" quantity="7.8" opacity="1" />
<ColorMapEntry color="#78368d" quantity="7.85" opacity="1" />
<ColorMapEntry color="#793990" quantity="7.9" opacity="1" />
<ColorMapEntry color="#7a3b92" quantity="7.95" opacity="1" />
<ColorMapEntry color="#7c3f95" quantity="8.0" opacity="1" label="8" />
<ColorMapEntry color="#7d4197" quantity="8.05" opacity="1" />
<ColorMapEntry color="#7e449a" quantity="8.1" opacity="1" />
<ColorMapEntry color="#7f479d" quantity="8.15" opacity="1" />
<ColorMapEntry color="#814a9f" quantity="8.2" opacity="1" />
<ColorMapEntry color="#824da2" quantity="8.25" opacity="1" />
<ColorMapEntry color="#834fa4" quantity="8.3" opacity="1" />
<ColorMapEntry color="#8452a7" quantity="8.35" opacity="1" />
<ColorMapEntry color="#8756aa" quantity="8.4" opacity="1" />
<ColorMapEntry color="#8859ad" quantity="8.45" opacity="1" />
<ColorMapEntry color="#895baf" quantity="8.5" opacity="1" />
<ColorMapEntry color="#8a5eb2" quantity="8.55" opacity="1" />
<ColorMapEntry color="#8c61b5" quantity="8.6" opacity="1" />
<ColorMapEntry color="#8d64b8" quantity="8.65" opacity="1" />
<ColorMapEntry color="#8f67ba" quantity="8.7" opacity="1" />
<ColorMapEntry color="#8f69bc" quantity="8.75" opacity="1" />
<ColorMapEntry color="#916dbf" quantity="8.8" opacity="1" />
<ColorMapEntry color="#926fc2" quantity="8.85" opacity="1" />
<ColorMapEntry color="#9472c4" quantity="8.9" opacity="1" />
<ColorMapEntry color="#9575c7" quantity="8.95" opacity="1" />
<ColorMapEntry color="#9778ca" quantity="9.0" opacity="1" label="9" />
<ColorMapEntry color="#987bcc" quantity="9.05" opacity="1" />
<ColorMapEntry color="#997dce" quantity="9.1" opacity="1" />
<ColorMapEntry color="#9a80d1" quantity="9.15" opacity="1" />
<ColorMapEntry color="#9d84d5" quantity="9.2" opacity="1" />
<ColorMapEntry color="#9e87d8" quantity="9.25" opacity="1" />
<ColorMapEntry color="#9f8ada" quantity="9.3" opacity="1" />
<ColorMapEntry color="#a08ddd" quantity="9.35" opacity="1" />
<ColorMapEntry color="#a28fdf" quantity="9.4" opacity="1" />
<ColorMapEntry color="#a393e2" quantity="9.45" opacity="1" />
<ColorMapEntry color="#a595e5" quantity="9.5" opacity="1" />
<ColorMapEntry color="#a598e7" quantity="9.55" opacity="1" />
<ColorMapEntry color="#a79bea" quantity="9.6" opacity="1" />
<ColorMapEntry color="#a89eec" quantity="9.65" opacity="1" />
<ColorMapEntry color="#aaa1ef" quantity="9.7" opacity="1" />
<ColorMapEntry color="#aba3f2" quantity="9.75" opacity="1" />
<ColorMapEntry color="#ada6f4" quantity="9.8" opacity="1" />
<ColorMapEntry color="#aea9f7" quantity="9.85" opacity="1" />
<ColorMapEntry color="#afacf9" quantity="9.9" opacity="1" />
<ColorMapEntry color="#b0aefc" quantity="9.95" opacity="1" />
<ColorMapEntry color="#b2b2ff" quantity="10.0" opacity="1" label="10" />
            </ColorMap>
          </RasterSymbolizer>
        </Rule>
      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>