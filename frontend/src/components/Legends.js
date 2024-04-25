import wxColorMap from '../pages/wxColorMap';
import { qpfColorMap } from '../pages/qpfColorMap';
import { tempColorMap } from '../pages/tempColorMap';
import { useMemo } from 'react';

export function WXLegend() {
  return (
    <div className="mt-4">
      <p className="font-notosansjp text-custom-black">Weather Forecast Legend:</p>
      <div className="mt-4 flex flex-wrap">
        {Object.entries(wxColorMap).map(([key, color]) => (
          <div key={key} className="flex items-center w-1/3" >
            <div className="w-4 h-4" style={{ backgroundColor: color, border: '1px solid black' }}></div>
            <p className="font-notosansjp text-custom-black ml-2 text-xs">{key}</p>
          </div>
        ))}
      </div>
    </div>
  )
}

function ColorRampLegend({ colorMap, min, max, title }) {
  const numDivisions = 4;
  let divisions = useMemo(() => {
    let range = max - min
    let result = []
    for (let i  = 0; i < numDivisions; i++) {
      result.push((i + 1) * range / numDivisions + min)
    }
    return result
  }, [min, max])

  return (
    <div className="mt-4">
      <p className="font-notosansjp text-custom-black">{title}</p>
      <div className="mt-4 flex items-center">
        <div className="w-full flex justify-between">
          <div className="w-1/12 font-notosansjp text-custom-black">{min}</div>
          <div className="w-10/12 h-6 flex justify-between">
            {qpfColorMap.slice(1, -1).map((color, index) => (
              <div key={index} className="w-1/6 h-full" style={{ backgroundColor: color }}></div>
            ))}
          </div>
          <div className="w-1/12 font-notosansjp text-custom-black text-right">{max}</div>
        </div>
      </div>
      <div className="mt-2 flex justify-between">
        <div className="w-1/12"></div>
        <div className='w-10/12 flex justify-around'>
          {divisions.map((value, index) => (
            <div key={index} className="font-notosansjp text-custom-black w-0 flex justify-center">{value}</div>
          ))}
        </div>
        <div className="w-1/12"></div>
      </div>
    </div>
  )
}

export function PrecipitationLegend() {
  return <ColorRampLegend colorMap={qpfColorMap.slice(1, -1)} min={0} max={10} title={'Quantitative Precipitation Forecast (QPF) Legend (inches):'} />
}

export function TemperatureLegend() {
  return <ColorRampLegend colorMap={tempColorMap.slice(1, -1)} min={-10} max={110} title={'Temperature Legend (°F):'} />
}