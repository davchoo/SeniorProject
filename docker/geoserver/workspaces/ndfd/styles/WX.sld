<?xml version="1.0" encoding="UTF-8"?>
<StyledLayerDescriptor version="1.0.0"
    xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd"
    xmlns="http://www.opengis.net/sld"
    xmlns:ogc="http://www.opengis.net/ogc"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <NamedLayer>
    <Name>NDFD Weather</Name>
    <UserStyle>
      <Title>Weather</Title>
      <FeatureTypeStyle>
        <Rule>
          <RasterSymbolizer>
            <ColorMap type="values">
              	<!-- None -->
				<ColorMapEntry color="#000000" quantity="0" opacity="0" label="None" />
				<ColorMapEntry color="#000000" quantity="1" opacity="0" label="None"/>
				<ColorMapEntry color="#000000" quantity="2" opacity="0" label="None" />
              	<!-- Rain -->
				<ColorMapEntry color="#c6ffad" quantity="3" opacity="1" label="LoProb Rain" />
				<ColorMapEntry color="#00ce00" quantity="4" opacity="1" label="HiProb Rain" />
				<ColorMapEntry color="#000000" quantity="5" opacity="0" label="None" />
              	<!-- Ice -->
				<ColorMapEntry color="#e0c8fc" quantity="6" opacity="1" label="LoProb Ice" />
				<ColorMapEntry color="#ce9cff" quantity="7" opacity="1" label="HiProb Ice" />
				<ColorMapEntry color="#000000" quantity="8" opacity="0" label="None" />
              	<!-- Snow -->
				<ColorMapEntry color="#8b96ff" quantity="9" opacity="1" label="LoProb Snow" />
				<ColorMapEntry color="#0031ff" quantity="10" opacity="1" label="HiProb Snow" />
				<ColorMapEntry color="#000000" quantity="11" opacity="0" label="None" />
              	<!-- Mix -->
				<ColorMapEntry color="#c5fffe" quantity="12" opacity="1" label="LoProb Mix" />
				<ColorMapEntry color="#00deef" quantity="13" opacity="1" label="HiProb Mix" />
				<ColorMapEntry color="#000000" quantity="14" opacity="0" label="None" />
              	<!-- Severe -->
				<ColorMapEntry color="#8B0000" quantity="15" opacity="1" label="Severe" />
				<ColorMapEntry color="#8B0000" quantity="16" opacity="1" label="Severe" />
				<ColorMapEntry color="#8B0000" quantity="17" opacity="1" label="Severe"/>
              	<!-- Fog -->
				<ColorMapEntry color="#ffff00" quantity="18" opacity="1" label="Fog" />
				<ColorMapEntry color="#ffff00" quantity="19" opacity="1" label="Fog" />
				<ColorMapEntry color="#ffff00" quantity="20" opacity="1" label="Fog" />
              	<!-- Smoke -->
				<ColorMapEntry color="#949494" quantity="21" opacity="1" label="Smoke" />
				<ColorMapEntry color="#949494" quantity="22" opacity="1" label="Smoke" />
				<ColorMapEntry color="#949494" quantity="23" opacity="1" label="Smoke" />
              	<!-- Blowing Snow/Dust -->
				<ColorMapEntry color="#e88112" quantity="24" opacity="1" label="Blowing" />
				<ColorMapEntry color="#e88112" quantity="25" opacity="1" label="Blowing" />
				<ColorMapEntry color="#e88112" quantity="26" opacity="1" label="Blowing" />
              	<!-- Haze -->
				<ColorMapEntry color="#cecece" quantity="27" opacity="1" label="Haze" />
				<ColorMapEntry color="#cecece" quantity="28" opacity="1" label="Haze" />
				<ColorMapEntry color="#cecece" quantity="29" opacity="1" label="Haze" />
            </ColorMap>
          </RasterSymbolizer>
        </Rule>
      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>